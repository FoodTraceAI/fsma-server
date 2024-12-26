// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.Location
import com.foodtraceai.model.TraceLotCode
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

private const val CTE_IPACK_SPROUTS_BASE_URL = "/api/v1/cte/ipacksprouts"
private const val CTE_IPACK_SPROUTS_ALT_BASE_URL = "/api/v1/cte/ipack-sprouts"
private const val CTE_IPACK_SPROUTS_ALT2_BASE_URL = "/api/v1/cte/ipack/sprouts"

@RestController
@RequestMapping(value = [CTE_IPACK_SPROUTS_BASE_URL,CTE_IPACK_SPROUTS_ALT_BASE_URL,
    CTE_IPACK_SPROUTS_ALT2_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteIPackSproutsController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/ipacksprouts/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
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
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackSproutsDto> {
        val location = getLocation(cteIPackSproutsDto.locationId,fsmaUser)
        val harvestLocation = getLocation(cteIPackSproutsDto.harvestLocationId,fsmaUser)

        val harvestFoodBus = getFoodBus(cteIPackSproutsDto.harvestBusinessId,fsmaUser)

        var coolLocation: Location? = null
        if (cteIPackSproutsDto.coolLocationId != null)
            coolLocation = getLocation(cteIPackSproutsDto.coolLocationId,fsmaUser)

        val packTlc = getTraceLotCode(cteIPackSproutsDto.packTlcId,fsmaUser)

        var packTlcSource: Location? = null
        if (cteIPackSproutsDto.packTlcSourceId != null)
            packTlcSource = getLocation(cteIPackSproutsDto.packTlcSourceId,fsmaUser)

        var seedGrowerLocation: Location? = null
        if (cteIPackSproutsDto.seedGrowerLocationId != null)
            seedGrowerLocation = getLocation(cteIPackSproutsDto.seedGrowerLocationId,fsmaUser)
        val seedConditionerLocation = getLocation(cteIPackSproutsDto.seedConditionerLocationId,fsmaUser)

        val seedTlc = getTraceLotCode(cteIPackSproutsDto.seedTlcId,fsmaUser)

        val seedPackingHouseLocation = getLocation(cteIPackSproutsDto.seedPackingHouseLocationId,fsmaUser)

        var seedPackingHouseTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedPackingHouseTlcId != null)
            seedPackingHouseTlc = getTraceLotCode(cteIPackSproutsDto.seedPackingHouseTlcId,fsmaUser)

        val seedSupplierLocation = getLocation(cteIPackSproutsDto.seedSupplierLocationId,fsmaUser)

        var seedSupplierTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedSupplierTlcId != null)
            seedSupplierTlc = getTraceLotCode(cteIPackSproutsDto.seedSupplierTlcId,fsmaUser)

        val cteIPackSprouts = cteIPackSproutsDto.toCteIPackSprouts(
            location, harvestLocation, harvestFoodBus, coolLocation, packTlc, packTlcSource,
            seedGrowerLocation, seedConditionerLocation, seedTlc, seedPackingHouseLocation, seedPackingHouseTlc,
            seedSupplierLocation, seedSupplierTlc,
        )
        val cteIPackSproutsResponse = cteIPackSproutsService.insert(cteIPackSprouts).toCteIPackSproutsDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_SPROUTS_BASE_URL.plus("/${cteIPackSproutsResponse.id}")))
            .body(cteIPackSproutsResponse)
    }

    // -- Update an existing CteIPackSproutsDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackSproutsDto: CteIPackSproutsDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackSproutsDto> {
        if (cteIPackSproutsDto.id <= 0L || cteIPackSproutsDto.id != id)
            throw UnauthorizedRequestException("Conflicting cteIPackSproutsDto Ids specified: $id != ${cteIPackSproutsDto.id}")

        val location = getLocation(cteIPackSproutsDto.locationId,fsmaUser)
         val harvestLocation = getLocation(cteIPackSproutsDto.harvestLocationId,fsmaUser)

        val harvestFoodBus = getFoodBus(cteIPackSproutsDto.harvestBusinessId,fsmaUser)

        var coolLocation: Location? = null
        if (cteIPackSproutsDto.coolLocationId != null)
            coolLocation = getLocation(cteIPackSproutsDto.coolLocationId,fsmaUser)

        val packTlc = getTraceLotCode(cteIPackSproutsDto.packTlcId,fsmaUser)

        var packTlcSource: Location? = null
        if (cteIPackSproutsDto.packTlcSourceId != null)
            packTlcSource = getLocation(cteIPackSproutsDto.packTlcSourceId,fsmaUser)

        var seedGrowerLocation: Location? = null
        if (cteIPackSproutsDto.seedGrowerLocationId != null)
            seedGrowerLocation = getLocation(cteIPackSproutsDto.seedGrowerLocationId,fsmaUser)

        val seedConditionerLocation = getLocation(cteIPackSproutsDto.seedConditionerLocationId,fsmaUser)

        val seedTlc = getTraceLotCode(cteIPackSproutsDto.seedTlcId,fsmaUser)

        val seedPackingHouseLocation = getLocation(cteIPackSproutsDto.seedPackingHouseLocationId,fsmaUser)

        var seedPackingHouseTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedPackingHouseTlcId != null)
            seedPackingHouseTlc = getTraceLotCode(cteIPackSproutsDto.seedPackingHouseTlcId,fsmaUser)

        val seedSupplierLocation = getLocation(cteIPackSproutsDto.seedSupplierLocationId,fsmaUser)

        var seedSupplierTlc: TraceLotCode? = null
        if (cteIPackSproutsDto.seedSupplierTlcId != null)
            seedSupplierTlc = getTraceLotCode(cteIPackSproutsDto.seedSupplierTlcId,fsmaUser)

        val cteIPackSprouts = cteIPackSproutsDto.toCteIPackSprouts(
            location, harvestLocation, harvestFoodBus, coolLocation, packTlc, packTlcSource,
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
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteIPackSproutsService.findById(id)?.let { cteIPackSprouts ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteIPackSproutsService.delete(cteIPackSprouts) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}