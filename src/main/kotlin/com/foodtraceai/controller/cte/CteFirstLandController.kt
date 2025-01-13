// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.Location
import com.foodtraceai.model.cte.CteFirstLandDto
import com.foodtraceai.model.cte.toCteFirstLand
import com.foodtraceai.model.cte.toCteFirstLandDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
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
    ): ResponseEntity<CteFirstLandDto> {
        val cteFirstLand = cteFirstLandService.findById(id)
            ?: throw EntityNotFoundException("CteFirstLand not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteFirstLand.toCteFirstLandDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteFirstLandDto: CteFirstLandDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteFirstLandDto> {
        val location = getLocation(cteFirstLandDto.locationId, fsmaUser)

        var tlcSource: Location? = null
        if (cteFirstLandDto.tlcSourceId != null)
            tlcSource = getLocation(cteFirstLandDto.tlcSourceId, fsmaUser)

        val cteFirstLand = cteFirstLandDto.toCteFirstLand(location, tlcSource)
        val cteFirstLandResponse = cteFirstLandService.insert(cteFirstLand).toCteFirstLandDto()
        return ResponseEntity.created(URI.create(CTE_FIRST_LAND_BASE_URL.plus("/${cteFirstLandResponse.id}")))
            .body(cteFirstLandResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteFirstLandDto: CteFirstLandDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteFirstLandDto> {
        if (cteFirstLandDto.id <= 0L || cteFirstLandDto.id != id)
            throw UnauthorizedRequestException("Conflicting CteFirstLandDto Ids specified: $id != ${cteFirstLandDto.id}")

        val location = getLocation(cteFirstLandDto.locationId, fsmaUser)

        var tlcSource: Location? = null
        if (cteFirstLandDto.tlcSourceId != null)
            tlcSource = getLocation(cteFirstLandDto.tlcSourceId, fsmaUser)

        val cteFirstLand = cteFirstLandDto.toCteFirstLand(location, tlcSource)
        val cteFirstLandResponse = cteFirstLandService.update(cteFirstLand).toCteFirstLandDto()
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