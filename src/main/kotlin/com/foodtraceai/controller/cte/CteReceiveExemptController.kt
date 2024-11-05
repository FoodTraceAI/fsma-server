// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceiveExemptDto
import com.foodtraceai.model.cte.toCteReceiveExempt
import com.foodtraceai.model.cte.toCteReceiveExemptDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_RECEIVE_EXEMPT_BASE_URL = "/api/v1/cte/receiveexempt"
private const val CTE_RECEIVE_EXEMPT_ALT_BASE_URL = "/api/v1/cte/receive-exempt"
private const val CTE_RECEIVE_EXEMPT_ALT2_BASE_URL = "/api/v1/cte/receive/exempt"

@RestController
@RequestMapping(
    value = [CTE_RECEIVE_EXEMPT_BASE_URL, CTE_RECEIVE_EXEMPT_ALT_BASE_URL,
        CTE_RECEIVE_EXEMPT_ALT2_BASE_URL]
)
@SecurityRequirement(name = "bearerAuth")
class cteReceiveExemptController : BaseController() {

    // -- Return a specific CteReceiveExempt
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteReceiveExemptDto> {
        val cteReceiveExempt = cteReceiveExemptService.findById(id)
            ?: throw EntityNotFoundException("cteReceiveExempt not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteReceiveExempt.toCteReceiveExemptDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteReceiveExemptDto: CteReceiveExemptDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteReceiveExemptDto> {
        val location = locationService.findById(cteReceiveExemptDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteReceiveExemptDto.locationId}")

        val traceLotCode = traceLotCodeService.findById(cteReceiveExemptDto.traceLotCodeId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteReceiveExemptDto.traceLotCodeId}")

        val shipFromLocation = locationService.findById(cteReceiveExemptDto.ipsLocationId)
            ?: throw EntityNotFoundException("ShipFromLocation not found: ${cteReceiveExemptDto.ipsLocationId}")

        val cteReceiveExempt = cteReceiveExemptDto.toCteReceiveExempt(
            traceLotCode, shipFromLocation, location
        )
        val cteReceiveExemptResponse = cteReceiveExemptService.insert(cteReceiveExempt).toCteReceiveExemptDto()
        return ResponseEntity.created(URI.create(CTE_RECEIVE_EXEMPT_BASE_URL.plus("/${cteReceiveExemptResponse.id}")))
            .body(cteReceiveExemptResponse)
    }

    // -- Update an existing CteReceiveExemptDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteReceiveExemptDto: CteReceiveExemptDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteReceiveExemptDto> {
        if (cteReceiveExemptDto.id <= 0L || cteReceiveExemptDto.id != id)
            throw UnauthorizedRequestException("Conflicting cteReceiveExempt Ids specified: $id != ${cteReceiveExemptDto.id}")

        val location = locationService.findById(cteReceiveExemptDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteReceiveExemptDto.locationId}")

        val traceLotCode = traceLotCodeService.findById(cteReceiveExemptDto.traceLotCodeId)
            ?: throw EntityNotFoundException("TraceLotCode not found: ${cteReceiveExemptDto.traceLotCodeId}")

        val shipFromLocation = locationService.findById(cteReceiveExemptDto.ipsLocationId)
            ?: throw EntityNotFoundException("ShipFromLocation not found: ${cteReceiveExemptDto.ipsLocationId}")

        val cteReceiveExempt = cteReceiveExemptDto.toCteReceiveExempt(traceLotCode, shipFromLocation, location)
        val cteReceiveExemptCto = cteReceiveExemptService.update(cteReceiveExempt).toCteReceiveExemptDto()
        return ResponseEntity.ok().body(cteReceiveExemptCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteReceiveExemptService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteReceiveExemptService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}