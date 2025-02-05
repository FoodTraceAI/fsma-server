// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteTransRequestDto
import com.foodtraceai.model.cte.CteTransResponseDto
import com.foodtraceai.model.cte.toCteTrans
import com.foodtraceai.model.cte.toCteTransResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_TRANSFORM_BASE_URL = "/api/v1/cte/trans"

@RestController
@RequestMapping(value = [CTE_TRANSFORM_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteTransController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/trans/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteTransResponseDto> {
        val cteTransform = cteTransService.findById(id)
            ?: throw EntityNotFoundException("CteTransform not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteTransform.toCteTransResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteTransRequestDto: CteTransRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteTransResponseDto> {
        val location = getLocation(cteTransRequestDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteTransRequestDto.inputTlcId, fsmaUser)
        val transformLotCode = getTraceLotCode(cteTransRequestDto.tlcId, fsmaUser)
        val transformFromLocation = getLocation(cteTransRequestDto.tlcSourceId, fsmaUser)
        val cteTransform = cteTransRequestDto.toCteTrans(
            id = 0, location, traceLotCode, transformLotCode, transformFromLocation
        )
        val cteTransformResponse = cteTransService.insert(cteTransform).toCteTransResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_TRANSFORM_BASE_URL.plus("/${cteTransformResponse.id}")))
            .body(cteTransformResponse)
    }

    // -- Update an existing CteTransDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteTransRequestDto: CteTransRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteTransResponseDto> {
        val location = getLocation(cteTransRequestDto.locationId, fsmaUser)
        val traceLotCode = getTraceLotCode(cteTransRequestDto.inputTlcId, fsmaUser)
        val transformLotCode = getTraceLotCode(cteTransRequestDto.tlcId, fsmaUser)
        val transformFromLocation = getLocation(cteTransRequestDto.tlcSourceId, fsmaUser)
        val cteTransform = cteTransRequestDto.toCteTrans(
            id = id, location, traceLotCode, transformLotCode, transformFromLocation
        )
        val cteTransformResponse = cteTransService.insert(cteTransform).toCteTransResponseDto()
        return ResponseEntity.ok().body(cteTransformResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteTransService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteTransService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}