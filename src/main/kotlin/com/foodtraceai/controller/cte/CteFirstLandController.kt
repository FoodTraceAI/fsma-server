// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteFirstLandRequestDto
import com.foodtraceai.model.cte.CteFirstLandResponseDto
import com.foodtraceai.model.cte.toCteFirstLand
import com.foodtraceai.model.cte.toCteFirstLandResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_FIRST_LAND_BASE_URL = "/api/v1/cte/firstland"
private const val CTE_FIRST_LAND_ALT_BASE_URL = "/api/v1/cte/first-land"

@RestController
@RequestMapping(value = [CTE_FIRST_LAND_BASE_URL, CTE_FIRST_LAND_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteFirstLandController : BaseController() {

    // -- Return a specific CteFirstLand
    // -- http://localhost:8080/api/v1/cte/firstland/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteFirstLandResponseDto> {
        val cteFirstLand = cteFirstLandService.findById(id)
            ?: throw EntityNotFoundException("CteFirstLand not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteFirstLand.toCteFirstLandResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteFirstLandRequestDto: CteFirstLandRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteFirstLandResponseDto> {
        val location = getLocation(cteFirstLandRequestDto.locationId, fsmaUser)
        val tlc = getTraceLotCode(cteFirstLandRequestDto.tlcId, fsmaUser)
        val tlcSource = getLocation(cteFirstLandRequestDto.tlcSourceId, fsmaUser)
        val cteFirstLand = cteFirstLandRequestDto.toCteFirstLand(id = 0, location, tlc, tlcSource)
        val cteFirstLandResponse = cteFirstLandService.insert(cteFirstLand).toCteFirstLandResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_FIRST_LAND_BASE_URL.plus("/${cteFirstLandResponse.id}")))
            .body(cteFirstLandResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteFirstLandRequestDto: CteFirstLandRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteFirstLandResponseDto> {
        val location = getLocation(cteFirstLandRequestDto.locationId, fsmaUser)
        val tlc = getTraceLotCode(cteFirstLandRequestDto.tlcId, fsmaUser)
        val tlcSource = getLocation(cteFirstLandRequestDto.tlcSourceId, fsmaUser)
        val cteFirstLand = cteFirstLandRequestDto.toCteFirstLand(id = id, location, tlc, tlcSource)
        val cteFirstLandResponse = cteFirstLandService.update(cteFirstLand).toCteFirstLandResponseDto()
        return ResponseEntity.ok().body(cteFirstLandResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteFirstLandService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteFirstLandService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}