// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteIPackSproutsRequestDto
import com.foodtraceai.model.cte.CteIPackSproutsResponseDto
import com.foodtraceai.model.cte.toCteIPackSprouts
import com.foodtraceai.model.cte.toCteIPackSproutsResponseDto
import com.foodtraceai.util.EntityNotFoundException
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
@RequestMapping(
    value = [CTE_IPACK_SPROUTS_BASE_URL, CTE_IPACK_SPROUTS_ALT_BASE_URL,
        CTE_IPACK_SPROUTS_ALT2_BASE_URL]
)
@SecurityRequirement(name = "bearerAuth")
class CteIPackSproutsController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/ipacksprouts/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackSproutsResponseDto> {
        val cteSprouts = cteIPackSproutsService.findById(id)
            ?: throw EntityNotFoundException("CteSprouts not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteSprouts.toCteIPackSproutsResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteIPackSproutsRequestDto: CteIPackSproutsRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackSproutsResponseDto> {
        val location = getLocation(cteIPackSproutsRequestDto.locationId, fsmaUser)
        val harvestLocation = getLocation(cteIPackSproutsRequestDto.harvestLocationId, fsmaUser)
        val harvestFoodBus = getFoodBus(cteIPackSproutsRequestDto.harvestBusinessId, fsmaUser)
        val coolLocation = cteIPackSproutsRequestDto.coolLocationId?.let {
            getLocation(it, fsmaUser)
        }
        val packTlc = getTraceLotCode(cteIPackSproutsRequestDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackSproutsRequestDto.packTlcSourceId?.let {
            getLocation(it, fsmaUser)
        }
        val seedGrowerLocation = cteIPackSproutsRequestDto.seedGrowerLocationId?.let {
            getLocation(it, fsmaUser)
        }
        val seedConditionerLocation = getLocation(cteIPackSproutsRequestDto.seedConditionerLocationId, fsmaUser)
        val seedTlc = getTraceLotCode(cteIPackSproutsRequestDto.seedTlcId, fsmaUser)
        val seedPackingHouseLocation = getLocation(
            cteIPackSproutsRequestDto.seedPackingHouseLocationId, fsmaUser
        )
        val seedPackingHouseTlc = cteIPackSproutsRequestDto.seedPackingHouseTlcId?.let {
            getTraceLotCode(it, fsmaUser)
        }
        val seedSupplierLocation = getLocation(cteIPackSproutsRequestDto.seedSupplierLocationId, fsmaUser)
        val seedSupplierTlc = cteIPackSproutsRequestDto.seedSupplierTlcId?.let {
            getTraceLotCode(it, fsmaUser)
        }
        val cteIPackSprouts = cteIPackSproutsRequestDto.toCteIPackSprouts(
            id = 0, location, harvestLocation, harvestFoodBus, coolLocation, packTlc, packTlcSource,
            seedGrowerLocation, seedConditionerLocation, seedTlc, seedPackingHouseLocation, seedPackingHouseTlc,
            seedSupplierLocation, seedSupplierTlc,
        )
        val cteIPackSproutsResponse = cteIPackSproutsService.insert(cteIPackSprouts).toCteIPackSproutsResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_IPACK_SPROUTS_BASE_URL.plus("/${cteIPackSproutsResponse.id}")))
            .body(cteIPackSproutsResponse)
    }

    // -- Update an existing CteIPackSproutsDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackSproutsRequestDto: CteIPackSproutsRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackSproutsResponseDto> {
        val location = getLocation(cteIPackSproutsRequestDto.locationId, fsmaUser)
        val harvestLocation = getLocation(cteIPackSproutsRequestDto.harvestLocationId, fsmaUser)
        val harvestFoodBus = getFoodBus(cteIPackSproutsRequestDto.harvestBusinessId, fsmaUser)
        val coolLocation = cteIPackSproutsRequestDto.coolLocationId?.let {
            getLocation(it, fsmaUser)
        }
        val packTlc = getTraceLotCode(cteIPackSproutsRequestDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackSproutsRequestDto.packTlcSourceId?.let {
            getLocation(it, fsmaUser)
        }
        val seedGrowerLocation = cteIPackSproutsRequestDto.seedGrowerLocationId?.let {
            getLocation(it, fsmaUser)
        }
        val seedConditionerLocation = getLocation(cteIPackSproutsRequestDto.seedConditionerLocationId, fsmaUser)
        val seedTlc = getTraceLotCode(cteIPackSproutsRequestDto.seedTlcId, fsmaUser)
        val seedPackingHouseLocation = getLocation(
            cteIPackSproutsRequestDto.seedPackingHouseLocationId, fsmaUser
        )
        val seedPackingHouseTlc = cteIPackSproutsRequestDto.seedPackingHouseTlcId?.let {
            getTraceLotCode(it, fsmaUser)
        }
        val seedSupplierLocation = getLocation(cteIPackSproutsRequestDto.seedSupplierLocationId, fsmaUser)
        val seedSupplierTlc = cteIPackSproutsRequestDto.seedSupplierTlcId?.let {
            getTraceLotCode(it, fsmaUser)
        }
        val cteIPackSprouts = cteIPackSproutsRequestDto.toCteIPackSprouts(
            id = id, location, harvestLocation, harvestFoodBus, coolLocation, packTlc, packTlcSource,
            seedGrowerLocation, seedConditionerLocation, seedTlc, seedPackingHouseLocation, seedPackingHouseTlc,
            seedSupplierLocation, seedSupplierTlc,
        )
        val cteIPackSproutsResponse = cteIPackSproutsService.update(cteIPackSprouts).toCteIPackSproutsResponseDto()
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