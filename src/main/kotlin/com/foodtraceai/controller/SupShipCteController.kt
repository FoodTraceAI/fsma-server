// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.SupShipCteDto
import com.foodtraceai.model.toSupCteShip
import com.foodtraceai.model.toSupShipCteDto
import com.foodtraceai.util.SupCteStatus
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val SUP_SHIP_CTE_BASE_URL = "/api/v1/supplier/shipCte"
private const val SUP_SHIP_CTE_ALT_BASE_URL = "/api/v1/supShipCte"

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
    ): ResponseEntity<SupShipCteDto> {
        val supShipCte = getSupShipCte(id, fsmaUser)
        return ResponseEntity.ok(supShipCte.toSupShipCteDto())
    }

    @GetMapping("/findAll")
    fun findAll(
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<List<SupShipCteDto>> {
        val supShipCteList = supShipCteService.findAll(fsmaUser)
        return ResponseEntity.ok(supShipCteList.map { it.toSupShipCteDto() })
    }

    // TODO: Remove me. This API is for testing only
    // http://localhost:8080/api/v1/supplier/findShipCte?sscc=sscc1&tlcId=1&shipFromLocationId=1
    @GetMapping("/findShipCte")
    private fun findShipCte(
        @RequestParam(value = "sscc", required = true) sscc: String,
        @RequestParam(value = "tlcId", required = true) tlcId: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteDto?> {
        val supShipCte = supShipCteService.findSupShipCte(
            sscc = sscc,
            tlcId = tlcId,
            shipToLocationId = fsmaUser.location.id,
            supCteStatus = SupCteStatus.Pending,
        )

        return ResponseEntity.ok(supShipCte?.toSupShipCteDto())
    }

    // -- Create a new SupShipCteDto
    @PostMapping
    fun create(
        @Valid @RequestBody supShipCteDto: SupShipCteDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteDto> {
        val cteReceive = supShipCteDto.cteReceiveId?.let { getCteReceive(it, fsmaUser) }
        val tlc = getTraceLotCode(supShipCteDto.tlcId, fsmaUser)
        val shipToLocation = getLocation(supShipCteDto.shipToLocationId, fsmaUser)
        val shipFromLocation = getLocation(supShipCteDto.shipFromLocationId, fsmaUser)
        val tlcSource = getLocation(supShipCteDto.tlcSourceId, fsmaUser)
        val supShipCte = supShipCteDto.toSupCteShip(cteReceive, tlc, shipToLocation, shipFromLocation, tlcSource)
        val cteCoolResponse = supShipCteService.insert(supShipCte).toSupShipCteDto()
        return ResponseEntity.created(URI.create(SUP_SHIP_CTE_BASE_URL.plus("/${cteCoolResponse.id}")))
            .body(cteCoolResponse)
    }

    // -- Update an existing SupShipCteDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody supShipCteDto: SupShipCteDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteDto> {
        val cteReceive = supShipCteDto.cteReceiveId?.let { getCteReceive(id, fsmaUser) }
        val tlc = getTraceLotCode(supShipCteDto.tlcId, fsmaUser)
        val shipToLocation = getLocation(supShipCteDto.shipToLocationId, fsmaUser)
        val shipFromLocation = getLocation(supShipCteDto.shipFromLocationId, fsmaUser)
        val tlcSource = getLocation(supShipCteDto.tlcSourceId, fsmaUser)
        val supShipCte = supShipCteDto.toSupCteShip(cteReceive, tlc, shipToLocation, shipFromLocation, tlcSource)
        val cteCoolResponse = supShipCteService.insert(supShipCte).toSupShipCteDto()
        return ResponseEntity.ok().body(cteCoolResponse)
    }

    // -- Delete an existing SupShipCte
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteCoolService.findById(id)?.let { ctcCoolCto ->
            assertFsmaUserLocationMatchessToken(fsmaUser, ctcCoolCto.location.id)
            cteCoolService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}