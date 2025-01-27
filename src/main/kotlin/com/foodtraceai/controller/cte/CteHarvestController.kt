// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteHarvestRequestDto
import com.foodtraceai.model.cte.CteHarvestResponseDto
import com.foodtraceai.model.cte.toCteHarvest
import com.foodtraceai.model.cte.toCteHarvestResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_HARVEST_BASE_URL = "/api/v1/cte/harvest"

@RestController
@RequestMapping(value = [CTE_HARVEST_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteHarvestController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/harvest/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteHarvestResponseDto> {
        val cteHarvest = cteHarvestService.findById(id)
            ?: throw EntityNotFoundException("CteHarvest not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteHarvest.toCteHarvestResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteHarvestRequestDto: CteHarvestRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteHarvestResponseDto> {
        val location = getLocation(cteHarvestRequestDto.locationId, fsmaUser)
        val subsequentRecipient = getLocation(cteHarvestRequestDto.isrLocationId, fsmaUser)
        val harvestLocation = getLocation(cteHarvestRequestDto.harvestLocationId, fsmaUser)
        val cteHarvest = cteHarvestRequestDto.toCteHarvest(
            id = 0, location, subsequentRecipient, harvestLocation
        )
        val cteHarvestResponse = cteHarvestService.insert(cteHarvest).toCteHarvestResponseDto()
        return ResponseEntity.created(URI.create(CTE_HARVEST_BASE_URL.plus("/${cteHarvestResponse.id}")))
            .body(cteHarvestResponse)
    }

    // -- Update an existing CteHarvestDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteHarvestRequestDto: CteHarvestRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteHarvestResponseDto> {
        val location = getLocation(cteHarvestRequestDto.locationId, fsmaUser)
        val subsequentRecipient = getLocation(cteHarvestRequestDto.isrLocationId, fsmaUser)
        val harvestLocation = getLocation(cteHarvestRequestDto.harvestLocationId, fsmaUser)
        val cteHarvest = cteHarvestRequestDto.toCteHarvest(
            id = id, location, subsequentRecipient, harvestLocation
        )
        val cteHarvestCto = cteHarvestService.update(cteHarvest).toCteHarvestResponseDto()
        return ResponseEntity.ok().body(cteHarvestCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteHarvestService.findById(id)?.let { cte ->
            assertFsmaUserLocationMatches(cte.location.id, fsmaUser)
            cteHarvestService.delete(cte) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}