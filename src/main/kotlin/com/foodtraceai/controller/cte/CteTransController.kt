// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteTransDto
import com.foodtraceai.model.cte.toCteTrans
import com.foodtraceai.model.cte.toCteTransDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_TRANSFORM_BASE_URL = "/api/v1/ctetransform"
private const val CTE_TRANSFORM_ALT_BASE_URL = "/api/v1/cte-transform"
private const val CTE_TRANSFORM_ALT2_BASE_URL = "/api/v1/cte/trans"

@RestController
@RequestMapping(value = [CTE_TRANSFORM_BASE_URL, CTE_TRANSFORM_ALT_BASE_URL, CTE_TRANSFORM_ALT2_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteTransController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteTransDto> {
        val cteTransform = cteTransService.findById(id)
            ?: throw EntityNotFoundException("CteTransform not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteTransform.toCteTransDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteTransDto: CteTransDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteTransDto> {
        val location = locationService.findById(cteTransDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteTransDto.locationId}")

        val traceLotCode = traceLotCodeService.findById(cteTransDto.inputTlcId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteTransDto.inputTlcId}")

        val transformLotCode = traceLotCodeService.findById(cteTransDto.newTlcId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteTransDto.inputTlcId}")

        val transformFromLocation = locationService.findById(cteTransDto.newTlcLocationId)
            ?: throw EntityNotFoundException("TransformFromLocation not found: ${cteTransDto.newTlcLocationId}")

        val cteTransform = cteTransDto.toCteTrans(location, traceLotCode, transformLotCode, transformFromLocation)
        val cteTransformResponse = cteTransService.insert(cteTransform).toCteTransDto()
        return ResponseEntity.created(URI.create(CTE_TRANSFORM_BASE_URL.plus("/${cteTransformResponse.id}")))
            .body(cteTransformResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteTransDto: CteTransDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteTransDto> {
        if (cteTransDto.id <= 0L || cteTransDto.id != id)
            throw UnauthorizedRequestException("Conflicting cteTransDto Ids specified: $id != ${cteTransDto.id}")

        val location = locationService.findById(cteTransDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteTransDto.locationId}")

        val traceLotCode = traceLotCodeService.findById(cteTransDto.inputTlcId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteTransDto.inputTlcId}")

        val transformLotCode = traceLotCodeService.findById(cteTransDto.newTlcId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteTransDto.inputTlcId}")

        val transformFromLocation = locationService.findById(cteTransDto.newTlcLocationId)
            ?: throw EntityNotFoundException("TransformFromLocation not found: ${cteTransDto.newTlcLocationId}")

        val cteTransform = cteTransDto.toCteTrans(location, traceLotCode, transformLotCode, transformFromLocation)
        val cteTransformCto = cteTransService.update(cteTransform).toCteTransDto()
        return ResponseEntity.ok().body(cteTransformCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteTransService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteTransService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}