// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.Location
import com.foodtraceai.model.TraceLotCode
import com.foodtraceai.model.cte.CteHarvest
import com.foodtraceai.model.cte.CteIPackSproutsDto
import com.foodtraceai.model.cte.toCteIPackSprouts
import com.foodtraceai.model.cte.toCteIPackSproutsDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_IPACK_SPROUTS_BASE_URL = "/api/v1/ctesprouts"
private const val CTE_IPACK_SPROUTS_ALT_BASE_URL = "/api/v1/cte/sprouts"

@RestController
@RequestMapping(value = [CTE_IPACK_SPROUTS_BASE_URL, CTE_IPACK_SPROUTS_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteIPackSproutsController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackSproutsDto> {
        val cteSprouts = cteIPackSproutsService.findById(id)
            ?: throw EntityNotFoundException("CteSprouts not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteSprouts.toCteIPackSproutsDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteIPackSproutsDto: CteIPackSproutsDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackSproutsDto> {
        val location = locationService.findById(cteIPackSproutsDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteIPackSproutsDto.locationId}")

        var cteHarvest: CteHarvest? = null
        if (cteIPackSproutsDto.cteHarvestId != null)
            cteHarvest = cteHarvestService.findById(cteIPackSproutsDto.cteHarvestId)
                ?: throw EntityNotFoundException("CteHaarvest not found: ${cteIPackSproutsDto.cteHarvestId}")

        val harvestLocation = locationService.findById(cteIPackSproutsDto.harvestLocationId)
            ?: throw EntityNotFoundException("HarvestLocation not found: ${cteIPackSproutsDto.harvestLocationId}")

        val harvestFoodBus = foodBusService.findById(cteIPackSproutsDto.harvestBusinessId)
            ?: throw EntityNotFoundException("HarvestBus not found: ${cteIPackSproutsDto.harvestBusinessId}")

        var coolLocation: Location? = null
        if (cteIPackSproutsDto.coolLocationId != null)
            coolLocation = locationService.findById(cteIPackSproutsDto.coolLocationId)
                ?: throw EntityNotFoundException("CoolLocation not found: ${cteIPackSproutsDto.coolLocationId}")

        val packTlc = traceLotCodeService.findById(cteIPackSproutsDto.packTlcId)
            ?: throw EntityNotFoundException("PackTlc not found: ${cteIPackSproutsDto.packTlcId}")

        var packTlcSource: Location? = null
        if (cteIPackSproutsDto.packTlcSourceId != null)
            packTlcSource = locationService.findById(cteIPackSproutsDto.packTlcSourceId)
                ?: throw EntityNotFoundException("PackTlcSource not found: ${cteIPackSproutsDto.packTlcSourceId}")

        var seedGrowerLocation: Location? = null
        if (cteIPackSproutsDto.seedGrowerLocationId != null)
            seedGrowerLocation = locationService.findById(cteIPackSproutsDto.seedGrowerLocationId)
                ?: throw EntityNotFoundException("SeedGrowerLocation not found: ${cteIPackSproutsDto.seedGrowerLocationId}")

        val seedConditionerLocation = locationService.findById(cteIPackSproutsDto.seedConditionerLocationId)
            ?: throw EntityNotFoundException("SeedConditionerLocation not found: ${cteIPackSproutsDto.seedConditionerLocationId}")

        val seedTlc = traceLotCodeService.findById(cteIPackSproutsDto.seedTlcId)
            ?: throw EntityNotFoundException("SeedTlc not found: ${cteIPackSproutsDto.seedTlcId}")

        val seedPackingHouseLocation = locationService.findById(cteIPackSproutsDto.seedPackingHouseLocationId)
            ?: throw EntityNotFoundException("SeedPackingHouseLocation not found: ${cteIPackSproutsDto.seedPackingHouseLocationId}")

        var seedPackingHouseTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedPackingHouseTlcId != null)
            seedPackingHouseTlc = traceLotCodeService.findById(cteIPackSproutsDto.seedPackingHouseTlcId)
                ?: throw EntityNotFoundException("SeedPackingHouseTlc not found: ${cteIPackSproutsDto.seedPackingHouseTlcId}")

        val seedSupplierLocation = locationService.findById(cteIPackSproutsDto.seedSupplierLocationId)
            ?: throw EntityNotFoundException("SeedSupplierLocation not found: ${cteIPackSproutsDto.seedSupplierLocationId}")

        var seedSupplierTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedSupplierTlcId != null)
            seedSupplierTlc = traceLotCodeService.findById(cteIPackSproutsDto.seedSupplierTlcId)
                ?: throw EntityNotFoundException("SeedSupplierTlc not found: ${cteIPackSproutsDto.seedSupplierTlcId}")

        val cteIPackSprouts = cteIPackSproutsDto.toCteIPackSprouts(
            location, cteHarvest, harvestLocation, harvestFoodBus, coolLocation, packTlc, packTlcSource,
            seedGrowerLocation, seedConditionerLocation, seedTlc, seedPackingHouseLocation, seedPackingHouseTlc,
            seedSupplierLocation, seedSupplierTlc,
        )
        val cteIPackSproutsResponse = cteIPackSproutsService.insert(cteIPackSprouts).toCteIPackSproutsDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_SPROUTS_BASE_URL.plus("/${cteIPackSproutsResponse.id}")))
            .body(cteIPackSproutsResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackSproutsDto: CteIPackSproutsDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackSproutsDto> {
        if (cteIPackSproutsDto.id <= 0L || cteIPackSproutsDto.id != id)
            throw UnauthorizedRequestException("Conflicting cteIPackSproutsDto Ids specified: $id != ${cteIPackSproutsDto.id}")

        val location = locationService.findById(cteIPackSproutsDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteIPackSproutsDto.locationId}")

        var cteHarvest: CteHarvest? = null
        if (cteIPackSproutsDto.cteHarvestId != null)
            cteHarvest = cteHarvestService.findById(cteIPackSproutsDto.cteHarvestId)
                ?: throw EntityNotFoundException("CteHaarvest not found: ${cteIPackSproutsDto.cteHarvestId}")

        val harvestLocation = locationService.findById(cteIPackSproutsDto.harvestLocationId)
            ?: throw EntityNotFoundException("HarvestLocation not found: ${cteIPackSproutsDto.harvestLocationId}")

        val harvestFoodBus = foodBusService.findById(cteIPackSproutsDto.harvestBusinessId)
            ?: throw EntityNotFoundException("HarvestBus not found: ${cteIPackSproutsDto.harvestBusinessId}")

        var coolLocation: Location? = null
        if (cteIPackSproutsDto.coolLocationId != null)
            coolLocation = locationService.findById(cteIPackSproutsDto.coolLocationId)
                ?: throw EntityNotFoundException("CoolLocation not found: ${cteIPackSproutsDto.coolLocationId}")

        val packTlc = traceLotCodeService.findById(cteIPackSproutsDto.packTlcId)
            ?: throw EntityNotFoundException("PackTlc not found: ${cteIPackSproutsDto.packTlcId}")

        var packTlcSource: Location? = null
        if (cteIPackSproutsDto.packTlcSourceId != null)
            packTlcSource = locationService.findById(cteIPackSproutsDto.packTlcSourceId)
                ?: throw EntityNotFoundException("PackTlcSource not found: ${cteIPackSproutsDto.packTlcSourceId}")

        var seedGrowerLocation: Location? = null
        if (cteIPackSproutsDto.seedGrowerLocationId != null)
            seedGrowerLocation = locationService.findById(cteIPackSproutsDto.seedGrowerLocationId)
                ?: throw EntityNotFoundException("SeedGrowerLocation not found: ${cteIPackSproutsDto.seedGrowerLocationId}")

        val seedConditionerLocation = locationService.findById(cteIPackSproutsDto.seedConditionerLocationId)
            ?: throw EntityNotFoundException("SeedConditionerLocation not found: ${cteIPackSproutsDto.seedConditionerLocationId}")

        val seedTlc = traceLotCodeService.findById(cteIPackSproutsDto.seedTlcId)
            ?: throw EntityNotFoundException("SeedTlc not found: ${cteIPackSproutsDto.seedTlcId}")

        val seedPackingHouseLocation = locationService.findById(cteIPackSproutsDto.seedPackingHouseLocationId)
            ?: throw EntityNotFoundException("SeedPackingHouseLocation not found: ${cteIPackSproutsDto.seedPackingHouseLocationId}")

        var seedPackingHouseTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedPackingHouseTlcId != null)
            seedPackingHouseTlc = traceLotCodeService.findById(cteIPackSproutsDto.seedPackingHouseTlcId)
                ?: throw EntityNotFoundException("SeedPackingHouseTlc not found: ${cteIPackSproutsDto.seedPackingHouseTlcId}")

        val seedSupplierLocation = locationService.findById(cteIPackSproutsDto.seedSupplierLocationId)
            ?: throw EntityNotFoundException("SeedSupplierLocation not found: ${cteIPackSproutsDto.seedSupplierLocationId}")

        var seedSupplierTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedSupplierTlcId != null)
            seedSupplierTlc = traceLotCodeService.findById(cteIPackSproutsDto.seedSupplierTlcId)
                ?: throw EntityNotFoundException("SeedSupplierTlc not found: ${cteIPackSproutsDto.seedSupplierTlcId}")

        val cteIPackSprouts = cteIPackSproutsDto.toCteIPackSprouts(
            location, cteHarvest, harvestLocation, harvestFoodBus, coolLocation, packTlc, packTlcSource,
            seedGrowerLocation, seedConditionerLocation, seedTlc, seedPackingHouseLocation, seedPackingHouseTlc,
            seedSupplierLocation, seedSupplierTlc,
        )
        val cteIPackSproutsResponse = cteIPackSproutsService.update(cteIPackSprouts).toCteIPackSproutsDto()
        return ResponseEntity.ok().body(cteIPackSproutsResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteIPackSproutsService.findById(id)?.let { cteIPackSprouts ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteIPackSproutsService.delete(cteIPackSprouts) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}