// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceiveDto
import com.foodtraceai.model.cte.toCteReceive
import com.foodtraceai.model.cte.toCteReceiveDto
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime

private const val CTE_RECEIVE_BASE_URL = "/api/v1/cte/receive"

@RestController
@RequestMapping(value = [CTE_RECEIVE_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteReceiveController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/receive/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveDto> {
        val cteReceive = getCteReceive(id, fsmaUser)
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteReceive.toCteReceiveDto())
    }

    @GetMapping("/findAll")
    fun findAll(
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<List<CteReceiveDto>> {
        val cteReceiveList = cteReceiveService.findAll(fsmaUser)
        return ResponseEntity.ok(cteReceiveList.map { it.toCteReceiveDto() })
    }

    // -- Create a new CteReceiveDto
    @PostMapping
    fun create(
        @Valid @RequestBody cteReceiveDto: CteReceiveDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveDto> {
        val location = getLocation(cteReceiveDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteReceiveDto.tlcId, fsmaUser)
        val shipFromLocation = getLocation(cteReceiveDto.ipsLocationId, fsmaUser)
        val tlcSource = getLocation(cteReceiveDto.tlcSourceId, fsmaUser)

        val cteReceive = cteReceiveDto.toCteReceive(
            location, traceLotCode, shipFromLocation, tlcSource
        )
        val cteReceiveResponse = cteReceiveService.insert(cteReceive).toCteReceiveDto()
        return ResponseEntity.created(URI.create(CTE_RECEIVE_BASE_URL.plus("/${cteReceiveResponse.id}")))
            .body(cteReceiveResponse)
    }

    // -- Update an existing CteReceiveDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteReceiveDto: CteReceiveDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveDto> {
        if (cteReceiveDto.id <= 0L || cteReceiveDto.id != id)
            throw UnauthorizedRequestException("Conflicting CteReceive Ids specified: $id != ${cteReceiveDto.id}")

        val location = getLocation(cteReceiveDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteReceiveDto.tlcId, fsmaUser)
        val shipFromLocation = getLocation(cteReceiveDto.ipsLocationId, fsmaUser)
        val tlcSource = getLocation(cteReceiveDto.tlcSourceId, fsmaUser)

        val cteReceive = cteReceiveDto.toCteReceive(location, traceLotCode, shipFromLocation, tlcSource)
        val cteReceiveResponse = cteReceiveService.update(cteReceive).toCteReceiveDto()
        return ResponseEntity.ok().body(cteReceiveResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteReceiveService.findById(id)?.let { cte ->
            assertFsmaUserLocationMatchessToken(fsmaUser, cte.location.id)
            cteReceiveService.delete(cte) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }

    data class SupShipArgs(
        val sscc: String,
        val tlcId: Long,
        val receiveLocationId: Long,
        val receiveDate: LocalDate,
        val receiveTime: OffsetDateTime,
    )

    @PostMapping("/makeCteReceive")
    private fun makeCteReceive(
        @Valid @RequestBody supShipArgs: SupShipArgs,
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<CteReceiveDto> {
        assertFsmaUserLocationMatchessToken(fsmaUser, supShipArgs.receiveLocationId)
        val cteReceive = cteReceiveService.makeCteReceiveFromSupShipCte(
            supShipArgs.sscc,
            supShipArgs.tlcId,
            supShipArgs.receiveLocationId,
            supShipArgs.receiveDate,
            supShipArgs.receiveTime,
        )
        return ResponseEntity.ok(cteReceive.toCteReceiveDto())
    }
}