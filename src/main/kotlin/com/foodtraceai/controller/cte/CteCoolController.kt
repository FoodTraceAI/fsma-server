// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteCoolDto
import com.foodtraceai.model.cte.toCteCool
import com.foodtraceai.model.cte.toCteCoolDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
import java.net.URI

private const val CTE_COOL_BASE_URL = "/api/v1/ctecool"
private const val CTE_COOL_ALT_BASE_URL = "/api/v1/cte/cool"

@RestController
@RequestMapping(value = [CTE_COOL_BASE_URL, CTE_COOL_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteCoolController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteCoolDto> {
        val cteCool = cteCoolService.findById(id)
            ?: throw EntityNotFoundException("CteCool not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteCool.toCteCoolDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteCoolDto: CteCoolDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteCoolDto> {
        val location = locationService.findById(cteCoolDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteCoolDto.locationId}")

        val subsequentRecipient = locationService.findById(cteCoolDto.isrLocationId)
            ?: throw EntityNotFoundException("SubsequentRecipient Location not found: ${cteCoolDto.isrLocationId}")

        val cteCool = cteCoolDto.toCteCool(location, subsequentRecipient)
        val cteCoolResponse = cteCoolService.insert(cteCool).toCteCoolDto()
        return ResponseEntity.created(URI.create(CTE_COOL_BASE_URL.plus("/${cteCoolResponse.id}")))
            .body(cteCoolResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteCoolDto: CteCoolDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteCoolDto> {
        if (cteCoolDto.id <= 0L || cteCoolDto.id != id)
            throw UnauthorizedRequestException("Conflicting CtcCool Ids specified: $id != ${cteCoolDto.id}")

        val location = locationService.findById(cteCoolDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteCoolDto.locationId}")

        val subsequentRecipient = locationService.findById(cteCoolDto.isrLocationId)
            ?: throw EntityNotFoundException("SubsequentRecipient Location not found: ${cteCoolDto.isrLocationId}")

        val cteCool = cteCoolDto.toCteCool(location, subsequentRecipient)
        val cteCoolCto = cteCoolService.update(cteCool).toCteCoolDto()
        return ResponseEntity.ok().body(cteCoolCto)
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