// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceiveRequestDto
import com.foodtraceai.model.cte.CteReceiveResponseDto
import com.foodtraceai.model.cte.toCteReceive
import com.foodtraceai.model.cte.toCteReceiveResponseDto
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
    ): ResponseEntity<CteReceiveResponseDto> {
        val cteReceive = getCteReceive(id, fsmaUser)
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteReceive.toCteReceiveResponseDto())
    }

    // -- Create a new CteReceiveDto
    @PostMapping
    fun create(
        @Valid @RequestBody cteReceiveRequestDto: CteReceiveRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveResponseDto> {
        val location = getLocation(cteReceiveRequestDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteReceiveRequestDto.tlcId, fsmaUser)
        val shipFromLocation = getLocation(cteReceiveRequestDto.ipsLocationId, fsmaUser)
        val tlcSource = getLocation(cteReceiveRequestDto.tlcSourceId, fsmaUser)
        val cteReceive = cteReceiveRequestDto.toCteReceive(
            id = 0, location, traceLotCode, shipFromLocation, tlcSource
        )
        val cteReceiveResponse = cteReceiveService.insert(cteReceive).toCteReceiveResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_RECEIVE_BASE_URL.plus("/${cteReceiveResponse.id}")))
            .body(cteReceiveResponse)
    }

    // -- Update an existing CteReceiveDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteReceiveRequestDto: CteReceiveRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveResponseDto> {
        val location = getLocation(cteReceiveRequestDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteReceiveRequestDto.tlcId, fsmaUser)
        val shipFromLocation = getLocation(cteReceiveRequestDto.ipsLocationId, fsmaUser)
        val tlcSource = getLocation(cteReceiveRequestDto.tlcSourceId, fsmaUser)
        val cteReceive = cteReceiveRequestDto.toCteReceive(
            id = id, location, traceLotCode, shipFromLocation, tlcSource
        )
        val cteReceiveResponse = cteReceiveService.update(cteReceive).toCteReceiveResponseDto()
        return ResponseEntity.ok().body(cteReceiveResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteReceiveService.findById(id)?.let { cte ->
            assertFsmaUserLocationMatches(cte.location.id, fsmaUser)
            cteReceiveService.delete(cte) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/findAll")
    fun findAll(
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<List<CteReceiveResponseDto>> {
        val cteReceiveList = cteReceiveService.findAll(fsmaUser)
        return ResponseEntity.ok(cteReceiveList.map { it.toCteReceiveResponseDto() })
    }

    data class SupShipArgs(
        val sscc: String,
        val tlcId: Long,
        val receiveLocationId: Long,
        val receiveDate: LocalDate,
        val receiveTime: OffsetDateTime,
    )

    // API call for mobile scanning of product on loading dock
    @PostMapping("/makeCteReceive")
    private fun makeCteReceive(
        @Valid @RequestBody supShipArgs: SupShipArgs,
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<CteReceiveResponseDto> {
        assertFsmaUserLocationMatches(supShipArgs.receiveLocationId, fsmaUser)
        val cteReceive = cteReceiveService.makeCteReceiveFromSupShipCte(
            supShipArgs.sscc,
            supShipArgs.tlcId,
            supShipArgs.receiveLocationId,
            supShipArgs.receiveDate,
            supShipArgs.receiveTime,
        )
        return ResponseEntity.ok(cteReceive.toCteReceiveResponseDto())
    }
}