// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.Location
import com.foodtraceai.model.cte.CteIPackExemptDto
import com.foodtraceai.model.cte.toCteIPackExempt
import com.foodtraceai.model.cte.toCteIPackExemptDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_IPACK_EXEMPT_BASE_URL = "/api/v1/cteexempt"
private const val CTE_IPACK_EXEMPT_ALT_BASE_URL = "/api/v1/cte/exempt"

@RestController
@RequestMapping(value = [CTE_IPACK_EXEMPT_BASE_URL, CTE_IPACK_EXEMPT_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteIPackExemptController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackExemptDto> {
        val cteExempt = cteIPackExemptService.findById(id)
            ?: throw EntityNotFoundException("CteExempt not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteExempt.toCteIPackExemptDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteIPackExemptDto: CteIPackExemptDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackExemptDto> {
        val location = locationService.findById(cteIPackExemptDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteIPackExemptDto.locationId}")

        val sourceLocation = locationService.findById(cteIPackExemptDto.sourceLocationId)
            ?: throw EntityNotFoundException("SourceLocation not found: ${cteIPackExemptDto.sourceLocationId}")

        val packTlc = traceLotCodeService.findById(cteIPackExemptDto.packTlcId)
            ?: throw EntityNotFoundException("PackTlc not found: ${cteIPackExemptDto.packTlcId}")

        var packTlcSource: Location? = null
        if (cteIPackExemptDto.packTlcSourceId != null)
            packTlcSource = locationService.findById(cteIPackExemptDto.packTlcSourceId)
                ?: throw EntityNotFoundException("PackTlcSource not found: ${cteIPackExemptDto.packTlcSourceId}")

        val iPackExempt = cteIPackExemptDto.toCteIPackExempt(location, sourceLocation, packTlc, packTlcSource)
        val iPackExemptResponse = cteIPackExemptService.insert(iPackExempt).toCteIPackExemptDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_EXEMPT_BASE_URL.plus("/${iPackExemptResponse.id}")))
            .body(iPackExemptResponse)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackExemptDto: CteIPackExemptDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackExemptDto> {
        if (cteIPackExemptDto.id <= 0L || cteIPackExemptDto.id != id)
            throw UnauthorizedRequestException("Conflicting cteIPackExemptDto Ids specified: $id != ${cteIPackExemptDto.id}")

        val location = locationService.findById(cteIPackExemptDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteIPackExemptDto.locationId}")

        val sourceLocation = locationService.findById(cteIPackExemptDto.sourceLocationId)
            ?: throw EntityNotFoundException("SourceLocation not found: ${cteIPackExemptDto.sourceLocationId}")

        val packTlc = traceLotCodeService.findById(cteIPackExemptDto.packTlcId)
            ?: throw EntityNotFoundException("PackTlc not found: ${cteIPackExemptDto.packTlcId}")

        var packTlcSource: Location? = null
        if (cteIPackExemptDto.packTlcSourceId != null)
            packTlcSource = locationService.findById(cteIPackExemptDto.packTlcSourceId)
                ?: throw EntityNotFoundException("PackTlcSource not found: ${cteIPackExemptDto.packTlcSourceId}")

        val cteIPackExempt = cteIPackExemptDto.toCteIPackExempt(location, sourceLocation, packTlc, packTlcSource)
        val iPackExemptCto = cteIPackExemptService.update(cteIPackExempt).toCteIPackExemptDto()
        return ResponseEntity.ok().body(iPackExemptCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteIPackExemptService.findById(id)?.let { cteIPackExempt ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteIPackExemptService.delete(cteIPackExempt) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}