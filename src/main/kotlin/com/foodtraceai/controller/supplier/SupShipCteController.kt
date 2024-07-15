// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.supplier

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.Location
import com.foodtraceai.model.supplier.SupShipCteDto
import com.foodtraceai.model.supplier.toSupCteShip
import com.foodtraceai.model.supplier.toSupShipCteDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val SUP_SHIP_CTE_BASE_URL = "/api/v1/supshipcte"
private const val SUP_SHIP_CTE_ALT_BASE_URL = "/api/v1/supplier/shipcte"

@RestController
@RequestMapping(value = [SUP_SHIP_CTE_BASE_URL, SUP_SHIP_CTE_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class SupShipCteController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<SupShipCteDto> {
        val supShipCte = supShipCteService.findById(id)
            ?: throw EntityNotFoundException("SupShipCte not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(supShipCte.toSupShipCteDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody supShipCteDto: SupShipCteDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<SupShipCteDto> {
        val tlc = traceLotCodeService.findById(supShipCteDto.tlcId)
            ?: throw EntityNotFoundException("Tlc not found: ${supShipCteDto.tlcId}")

        val shipToLocation = locationService.findById(supShipCteDto.shipToLocationId)
            ?: throw EntityNotFoundException("ShipToLocation not found: ${supShipCteDto.shipToLocationId}")

        val shipFromLocation = locationService.findById(supShipCteDto.shipFromLocationId)
            ?: throw EntityNotFoundException("ShipFromLocation not found: ${supShipCteDto.shipFromLocationId}")

        var tlcSource: Location? = null
        if (supShipCteDto.tlcSourceId != null)
            tlcSource = locationService.findById(supShipCteDto.tlcSourceId)
                ?: throw EntityNotFoundException("TlcSourceLocation not found: ${supShipCteDto.tlcSourceId}")

        val supShipCte = supShipCteDto.toSupCteShip(tlc, shipToLocation, shipFromLocation, tlcSource)
        val cteCoolResponse = supShipCteService.insert(supShipCte).toSupShipCteDto()
        return ResponseEntity.created(URI.create(SUP_SHIP_CTE_BASE_URL.plus("/${cteCoolResponse.id}")))
            .body(cteCoolResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody supShipCteDto: SupShipCteDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<SupShipCteDto> {
        val tlc = traceLotCodeService.findById(supShipCteDto.tlcId)
            ?: throw EntityNotFoundException("Tlc not found: ${supShipCteDto.tlcId}")

        val shipToLocation = locationService.findById(supShipCteDto.shipToLocationId)
            ?: throw EntityNotFoundException("ShipToLocation not found: ${supShipCteDto.shipToLocationId}")

        val shipFromLocation = locationService.findById(supShipCteDto.shipFromLocationId)
            ?: throw EntityNotFoundException("ShipFromLocation not found: ${supShipCteDto.shipFromLocationId}")

        var tlcSource: Location? = null
        if (supShipCteDto.tlcSourceId != null)
            tlcSource = locationService.findById(supShipCteDto.tlcSourceId)
                ?: throw EntityNotFoundException("TlcSourceLocation not found: ${supShipCteDto.tlcSourceId}")

        val supShipCte = supShipCteDto.toSupCteShip(tlc, shipToLocation, shipFromLocation, tlcSource)
        val cteCoolResponse = supShipCteService.insert(supShipCte).toSupShipCteDto()
        return ResponseEntity.ok().body(cteCoolResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteCoolService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteCoolService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}