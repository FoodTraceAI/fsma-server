// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.*
import com.foodtraceai.util.SupCteStatus
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val SUP_SHIP_CTE_BASE_URL = "/api/v1/supplier/shipcte"
private const val SUP_SHIP_CTE_ALT_BASE_URL = "/api/v1/supshipcte"

@RestController
@RequestMapping(value = [SUP_SHIP_CTE_BASE_URL, SUP_SHIP_CTE_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class SupShipCteController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/supshipcte/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteResponseDto> {
        val supShipCte = getSupShipCte(id, fsmaUser)
        return ResponseEntity.ok(supShipCte.toSupShipCteResponseDto())
    }

    // -- Create a new SupShipCteDto
    @PostMapping
    fun create(
        @Valid @RequestBody supShipCteRequestDto: SupShipCteRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteResponseDto> {
        val cteReceive = supShipCteRequestDto.cteReceiveId?.let { getCteReceive(it, fsmaUser) }
        val tlc = getTraceLotCode(supShipCteRequestDto.tlcId, fsmaUser)
        val shipToLocation = getLocation(supShipCteRequestDto.shipToLocationId, fsmaUser)
        val shipFromLocation = getLocation(supShipCteRequestDto.shipFromLocationId, fsmaUser)
        val tlcSource = getLocation(supShipCteRequestDto.tlcSourceId, fsmaUser)
        val supShipCte =
            supShipCteRequestDto.toSupCteShip(id = 0, cteReceive, tlc, shipToLocation, shipFromLocation, tlcSource)
        val cteCoolResponse = supShipCteService.insert(supShipCte).toSupShipCteResponseDto()
        return ResponseEntity
            .created(URI.create(SUP_SHIP_CTE_BASE_URL.plus("/${cteCoolResponse.id}")))
            .body(cteCoolResponse)
    }

    // -- Update an existing SupShipCteDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody supShipCteRequestDto: SupShipCteRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteResponseDto> {
        val cteReceive = supShipCteRequestDto.cteReceiveId?.let { getCteReceive(id, fsmaUser) }
        val tlc = getTraceLotCode(supShipCteRequestDto.tlcId, fsmaUser)
        val shipToLocation = getLocation(supShipCteRequestDto.shipToLocationId, fsmaUser)
        val shipFromLocation = getLocation(supShipCteRequestDto.shipFromLocationId, fsmaUser)
        val tlcSource = getLocation(supShipCteRequestDto.tlcSourceId, fsmaUser)
        val supShipCte =
            supShipCteRequestDto.toSupCteShip(id=id, cteReceive, tlc, shipToLocation, shipFromLocation, tlcSource)
        val cteCoolResponse = supShipCteService.insert(supShipCte).toSupShipCteResponseDto()
        return ResponseEntity.ok().body(cteCoolResponse)
    }

    // -- Delete an existing SupShipCte
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        supShipCteService.findById(id)?.let { supShipCte ->
            assertFsmaUserLocationMatches(supShipCte.shipToLocation.id, fsmaUser)
            supShipCteService.delete(supShipCte) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/findAll")
    fun findAll(
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<List<SupShipCteResponseDto>> {
        val supShipCteList = supShipCteService.findAll(fsmaUser)
        return ResponseEntity.ok(supShipCteList.map { it.toSupShipCteResponseDto() })
    }

    // TODO: Remove me. This API is for testing only
    // http://localhost:8080/api/v1/supplier/findShipCte?sscc=sscc1&tlcId=1&shipFromLocationId=1
    @GetMapping("/findShipCte")
    private fun findShipCte(
        @RequestParam(value = "sscc", required = true) sscc: String,
        @RequestParam(value = "tlcId", required = true) tlcId: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteResponseDto?> {
        val supShipCte = supShipCteService.findSupShipCte(
            sscc = sscc,
            tlcId = tlcId,
            shipToLocationId = fsmaUser.location.id,
            supCteStatus = SupCteStatus.Pending,
        )

        return ResponseEntity.ok(supShipCte?.toSupShipCteResponseDto())
    }
}