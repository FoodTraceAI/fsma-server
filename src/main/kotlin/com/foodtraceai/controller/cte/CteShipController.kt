// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteShip
import com.foodtraceai.model.cte.CteShipDto
import com.foodtraceai.model.cte.toCteShip
import com.foodtraceai.model.cte.toCteShipDto
import com.foodtraceai.util.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime

private const val CTE_SHIP_BASE_URL = "/api/v1/cte/ship"

@RestController
@RequestMapping(value = [CTE_SHIP_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteShipController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/ship/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteShipDto> {
        val cteShip = cteShipService.findById(id)
            ?: throw EntityNotFoundException("CteShip not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteShip.toCteShipDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteShipDto: CteShipDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteShipDto> {
        val location = getLocation(cteShipDto.locationId, fsmaUser)

        val traceLotCode = getTraceLotCode(cteShipDto.tlcId, fsmaUser)

        val shipToLocation = getLocation(cteShipDto.shipToLocationId, fsmaUser)
        val tlcSource = getLocation(cteShipDto.tlcSourceId, fsmaUser)

        val cteShip = cteShipDto.toCteShip(traceLotCode, shipToLocation, location, tlcSource)
        val cteShipResponse = cteShipService.insert(cteShip).toCteShipDto()
        return ResponseEntity.created(URI.create(CTE_SHIP_BASE_URL.plus("/${cteShipResponse.id}")))
            .body(cteShipResponse)
    }

    // -- Update an existing CteShipDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteShipDto: CteShipDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteShipDto> {
        if (cteShipDto.id <= 0L || cteShipDto.id != id)
            throw UnauthorizedRequestException("Conflicting CteShip Ids specified: $id != ${cteShipDto.id}")

        val location = getLocation(cteShipDto.locationId, fsmaUser)

        val traceLotCode = getTraceLotCode(cteShipDto.tlcId, fsmaUser)

        val shipToLocation = getLocation(cteShipDto.shipToLocationId, fsmaUser)
        val tlcSource = getLocation(cteShipDto.tlcSourceId, fsmaUser)

        val cteShip = cteShipDto.toCteShip(traceLotCode, shipToLocation, location, tlcSource)
        val cteShipCto = cteShipService.update(cteShip).toCteShipDto()
        return ResponseEntity.ok().body(cteShipCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteShipService.findById(id)?.let { cteCoolCto ->
            assertFsmaUserLocationMatches(cteCoolCto.location.id, fsmaUser)
            cteShipService.delete(cteCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }

    data class CteShipArgs(
        val ftlItem: FtlItem,
        val tlcId: Long,
        val quantity: Int,
        val unitOfMeasure: UnitOfMeasure,
        val prodDesc: String = "",
        val variety: String = "",
        val shipToLocationId: Long,
        val locationId: Long,   // shipFromLocation
        val shipDate: LocalDate = LocalDate.now(),
        val shipTime: OffsetDateTime = OffsetDateTime.now(),
        val referenceDocumentType: ReferenceDocumentType,
        val referenceDocumentNum: String,
    )

    @PostMapping("/makeCteShip")
    private fun makeCteReceive(
        @Valid @RequestBody cteShipArgs: CteShipArgs,
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<CteShipDto> {
        assertFsmaUserLocationMatches(cteShipArgs.locationId, fsmaUser)
        val tlc = getTraceLotCode(cteShipArgs.tlcId, fsmaUser)
        val cteShipResponseDto = cteShipService.insert(
            CteShip(
                cteType = CteType.Ship,
                ftlItem = cteShipArgs.ftlItem,
                tlc = tlc,
                quantity = cteShipArgs.quantity,
                unitOfMeasure = cteShipArgs.unitOfMeasure,
                prodDesc = cteShipArgs.prodDesc,
                variety = cteShipArgs.variety,
                shipToLocation = getLocation(cteShipArgs.locationId),
                location = getLocation(cteShipArgs.locationId, fsmaUser),   // shipFromLocation
                shipDate = cteShipArgs.shipDate,
                shipTime = cteShipArgs.shipTime,
                tlcSource = tlc.tlcSource,
                tlcSourceReference = tlc.tlcSourceReference,
                referenceDocumentType = cteShipArgs.referenceDocumentType,
                referenceDocumentNum = cteShipArgs.referenceDocumentNum,
            )
        ).toCteShipDto()
        return ResponseEntity
            .created(URI.create(CTE_SHIP_BASE_URL.plus("/${cteShipResponseDto.id}")))
            .body(cteShipResponseDto)
    }
}