// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteShipDto
import com.foodtraceai.model.cte.toCteShip
import com.foodtraceai.model.cte.toCteShipDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_SHIP_BASE_URL = "/api/v1/cte/ship"

@RestController
@RequestMapping(value = [CTE_SHIP_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteShipController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
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
        val location = locationService.findById(cteShipDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteShipDto.locationId}")

        val traceLotCode = traceLotCodeService.findById(cteShipDto.tlcId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteShipDto.tlcId}")

        val shipToLocation = locationService.findById(cteShipDto.shipToLocationId)
            ?: throw EntityNotFoundException("ShipToLocation not found: ${cteShipDto.shipToLocationId}")

        val tlcSource = locationService.findById(cteShipDto.tlcSourceId)
            ?: throw EntityNotFoundException("TlcSource not found: ${cteShipDto.tlcSourceId}")

        val cteShip = cteShipDto.toCteShip( traceLotCode, shipToLocation,location, tlcSource)
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

        val location = locationService.findById(cteShipDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteShipDto.locationId}")

        val traceLotCode = traceLotCodeService.findById(cteShipDto.tlcId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteShipDto.tlcId}")

        val shipToLocation = locationService.findById(cteShipDto.shipToLocationId)
            ?: throw EntityNotFoundException("ShipToLocation not found: ${cteShipDto.shipToLocationId}")

        val tlcSource = locationService.findById(cteShipDto.tlcSourceId)
            ?: throw EntityNotFoundException("TlcSource not found: ${cteShipDto.tlcSourceId}")

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
        cteShipService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteShipService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}