// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteHarvestDto
import com.foodtraceai.model.cte.toCteHarvest
import com.foodtraceai.model.cte.toCteHarvestDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_HARVEST_BASE_URL = "/api/v1/cteharvest"
private const val CTE_HARVEST_ALT_BASE_URL = "/api/v1/cte/harvest"

@RestController
@RequestMapping(value = [CTE_HARVEST_BASE_URL, CTE_HARVEST_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteHarvestController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteHarvestDto> {
        val cteHarvest = cteHarvestService.findById(id)
            ?: throw EntityNotFoundException("CteHarvest not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteHarvest.toCteHarvestDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteHarvestDto: CteHarvestDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteHarvestDto> {
        val location = locationService.findById(cteHarvestDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteHarvestDto.locationId}")

        val subsequentRecipient = locationService.findById(cteHarvestDto.isrLocationId)
            ?: throw EntityNotFoundException("SubsequentRecipient not found: ${cteHarvestDto.isrLocationId}")

        val harvestLocation = locationService.findById(cteHarvestDto.harvestLocationId)
            ?: throw EntityNotFoundException("HarvestLocation not found: ${cteHarvestDto.harvestLocationId}")

        val cteHarvest = cteHarvestDto.toCteHarvest(location, subsequentRecipient, harvestLocation)
        val cteHarvestResponse = cteHarvestService.insert(cteHarvest).toCteHarvestDto()
        return ResponseEntity.created(URI.create(CTE_HARVEST_BASE_URL.plus("/${cteHarvestResponse.id}")))
            .body(cteHarvestResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteHarvestDto: CteHarvestDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteHarvestDto> {
        if (cteHarvestDto.id <= 0L || cteHarvestDto.id != id)
            throw UnauthorizedRequestException("Conflicting CteHarvest Ids specified: $id != ${cteHarvestDto.id}")

        val location = locationService.findById(cteHarvestDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteHarvestDto.locationId}")

        val subsequentRecipient = locationService.findById(cteHarvestDto.isrLocationId)
            ?: throw EntityNotFoundException("SubsequentRecipient Location not found: ${cteHarvestDto.isrLocationId}")

        val harvestLocation = locationService.findById(cteHarvestDto.harvestLocationId)
            ?: throw EntityNotFoundException("HarvestLocation not found: ${cteHarvestDto.harvestLocationId}")

        val cteHarvest = cteHarvestDto.toCteHarvest(location, subsequentRecipient, harvestLocation)
        val cteHarvestCto = cteHarvestService.update(cteHarvest).toCteHarvestDto()
        return ResponseEntity.ok().body(cteHarvestCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteHarvestService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteHarvestService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}