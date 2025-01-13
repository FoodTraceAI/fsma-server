// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
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
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackExemptDto> {
        val location = getLocation(cteIPackExemptDto.locationId,fsmaUser)
        val sourceLocation = getLocation(cteIPackExemptDto.sourceLocationId,fsmaUser)

        val packTlc = getTraceLotCode(cteIPackExemptDto.packTlcId,fsmaUser)

        var packTlcSource: Location? = null
        if (cteIPackExemptDto.packTlcSourceId != null)
            packTlcSource = getLocation(cteIPackExemptDto.packTlcSourceId,fsmaUser)

        val iPackExempt = cteIPackExemptDto.toCteIPackExempt(location, sourceLocation, packTlc, packTlcSource)
        val iPackExemptResponse = cteIPackExemptService.insert(iPackExempt).toCteIPackExemptDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_EXEMPT_BASE_URL.plus("/${iPackExemptResponse.id}")))
            .body(iPackExemptResponse)
    }

    // -- Update an existing CteIPackExemptDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackExemptDto: CteIPackExemptDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackExemptDto> {
        if (cteIPackExemptDto.id <= 0L || cteIPackExemptDto.id != id)
            throw UnauthorizedRequestException("Conflicting cteIPackExemptDto Ids specified: $id != ${cteIPackExemptDto.id}")

        val location = getLocation(cteIPackExemptDto.locationId,fsmaUser)
        val sourceLocation = getLocation(cteIPackExemptDto.sourceLocationId,fsmaUser)

        val packTlc = getTraceLotCode(cteIPackExemptDto.packTlcId,fsmaUser)

        var packTlcSource: Location? = null
        if (cteIPackExemptDto.packTlcSourceId != null)
            packTlcSource = getLocation(cteIPackExemptDto.packTlcSourceId,fsmaUser)

        val cteIPackExempt = cteIPackExemptDto.toCteIPackExempt(location, sourceLocation, packTlc, packTlcSource)
        val iPackExemptCto = cteIPackExemptService.update(cteIPackExempt).toCteIPackExemptDto()
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