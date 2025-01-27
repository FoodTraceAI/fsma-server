// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteShipRequestDto
import com.foodtraceai.model.cte.CteShipResponseDto
import com.foodtraceai.model.cte.toCteShip
import com.foodtraceai.model.cte.toCteShipResponseDto
import com.foodtraceai.util.EntityNotFoundException
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
    // -    http://localhost:8080/api/v1/cte/ship/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteShipResponseDto> {
        val cteShip = cteShipService.findById(id)
            ?: throw EntityNotFoundException("CteShip not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteShip.toCteShipResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteShipRequestDto: CteShipRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteShipResponseDto> {
        val location = getLocation(cteShipRequestDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteShipRequestDto.tlcId, fsmaUser)
        val shipToLocation = getLocation(cteShipRequestDto.shipToLocationId)
        val tlcSource = getLocation(cteShipRequestDto.tlcSourceId)
        val cteShip = cteShipRequestDto.toCteShip(
            id = 0, traceLotCode, shipToLocation, location, tlcSource
        )
        val cteShipResponse = cteShipService.insert(cteShip).toCteShipResponseDto()
        return ResponseEntity.created(URI.create(CTE_SHIP_BASE_URL.plus("/${cteShipResponse.id}")))
            .body(cteShipResponse)
    }

    // -- Update an existing CteShipDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteShipResponseDto: CteShipRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteShipResponseDto> {
        val location = getLocation(cteShipResponseDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteShipResponseDto.tlcId, fsmaUser)
        val shipToLocation = getLocation(cteShipResponseDto.shipToLocationId, fsmaUser)
        val tlcSource = getLocation(cteShipResponseDto.tlcSourceId, fsmaUser)
        val cteShip = cteShipResponseDto.toCteShip(
            id = id, traceLotCode, shipToLocation, location, tlcSource
        )
        val cteShipCto = cteShipService.update(cteShip).toCteShipResponseDto()
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
}