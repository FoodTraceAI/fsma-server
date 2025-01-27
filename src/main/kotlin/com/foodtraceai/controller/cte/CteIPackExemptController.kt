// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteIPackExemptRequestDto
import com.foodtraceai.model.cte.CteIPackExemptResponseDto
import com.foodtraceai.model.cte.toCteIPackExempt
import com.foodtraceai.model.cte.toCteIPackExemptResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_IPACK_EXEMPT_BASE_URL = "/api/v1/cte/ipackexempt"
private const val CTE_IPACK_EXEMPT_ALT_BASE_URL = "/api/v1/cte/ipack-exempt"
private const val CTE_IPACK_EXEMPT_ALT2_BASE_URL = "/api/v1/cte/ipack/exempt"

@RestController
@RequestMapping(
    value = [CTE_IPACK_EXEMPT_BASE_URL, CTE_IPACK_EXEMPT_ALT_BASE_URL,
        CTE_IPACK_EXEMPT_ALT2_BASE_URL]
)
@SecurityRequirement(name = "bearerAuth")
class CteIPackExemptController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/ipackexempt/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackExemptResponseDto> {
        val cteExempt = cteIPackExemptService.findById(id)
            ?: throw EntityNotFoundException("CteExempt not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteExempt.toCteIPackExemptResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteIPackExemptRequestDto: CteIPackExemptRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackExemptResponseDto> {
        val location = getLocation(cteIPackExemptRequestDto.locationId, fsmaUser)
        val sourceLocation = getLocation(cteIPackExemptRequestDto.sourceLocationId, fsmaUser)
        val packTlc = getTraceLotCode(cteIPackExemptRequestDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackExemptRequestDto.packTlcSourceId?.let {
            getLocation(it, fsmaUser)
        }
        val iPackExempt = cteIPackExemptRequestDto.toCteIPackExempt(
            id = 0, location, sourceLocation, packTlc, packTlcSource
        )
        val iPackExemptResponse = cteIPackExemptService.insert(iPackExempt).toCteIPackExemptResponseDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_EXEMPT_BASE_URL.plus("/${iPackExemptResponse.id}")))
            .body(iPackExemptResponse)
    }

    // -- Update an existing CteIPackExemptDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackExemptRequestDto: CteIPackExemptRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackExemptResponseDto> {
        val location = getLocation(cteIPackExemptRequestDto.locationId, fsmaUser)
        val sourceLocation = getLocation(cteIPackExemptRequestDto.sourceLocationId, fsmaUser)
        val packTlc = getTraceLotCode(cteIPackExemptRequestDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackExemptRequestDto.packTlcSourceId?.let {
            getLocation(it, fsmaUser)
        }
        val cteIPackExempt = cteIPackExemptRequestDto.toCteIPackExempt(
            id = id, location, sourceLocation, packTlc, packTlcSource
        )
        val iPackExemptCto = cteIPackExemptService.update(cteIPackExempt).toCteIPackExemptResponseDto()
        return ResponseEntity.ok().body(iPackExemptCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteIPackExemptService.findById(id)?.let { cteIPackExempt ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteIPackExemptService.delete(cteIPackExempt) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}