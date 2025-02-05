// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceiveExemptRequestDto
import com.foodtraceai.model.cte.CteReceiveExemptResponseDto
import com.foodtraceai.model.cte.toCteReceiveExempt
import com.foodtraceai.model.cte.toCteReceiveExemptResponseDto
import com.foodtraceai.util.EntityNotFoundException
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
    // -    http://localhost:8080/api/v1/cte/receiveexempt/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveExemptResponseDto> {
        val cteReceiveExempt = cteReceiveExemptService.findById(id)
            ?: throw EntityNotFoundException("cteReceiveExempt not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteReceiveExempt.toCteReceiveExemptResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteReceiveExemptRequestDto: CteReceiveExemptRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveExemptResponseDto> {
        val traceLotCode = getTraceLotCode(cteReceiveExemptRequestDto.traceLotCodeId, fsmaUser)
        val shipFromLocation = getLocation(cteReceiveExemptRequestDto.ipsLocationId, fsmaUser)
        val location = getLocation(cteReceiveExemptRequestDto.locationId, fsmaUser)
        val tlcSource = getLocation(cteReceiveExemptRequestDto.tlcSourceId, fsmaUser)
        val cteReceiveExempt = cteReceiveExemptRequestDto.toCteReceiveExempt(
            id = 0, traceLotCode, shipFromLocation, location, tlcSource,
        )
        val cteReceiveExemptResponse = cteReceiveExemptService.insert(cteReceiveExempt).toCteReceiveExemptResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_RECEIVE_EXEMPT_BASE_URL.plus("/${cteReceiveExemptResponse.id}")))
            .body(cteReceiveExemptResponse)
    }

    // -- Update an existing CteReceiveExemptDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteReceiveExemptRequestDto: CteReceiveExemptRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteReceiveExemptResponseDto> {
        val traceLotCode = getTraceLotCode(cteReceiveExemptRequestDto.traceLotCodeId, fsmaUser)
        val shipFromLocation = getLocation(cteReceiveExemptRequestDto.ipsLocationId, fsmaUser)
        val location = getLocation(cteReceiveExemptRequestDto.locationId, fsmaUser)
        val tlcSource = getLocation(cteReceiveExemptRequestDto.tlcSourceId, fsmaUser)
        val cteReceiveExempt = cteReceiveExemptRequestDto.toCteReceiveExempt(
            id = id, traceLotCode, shipFromLocation, location, tlcSource,
        )
        val cteReceiveExemptResponse = cteReceiveExemptService.insert(cteReceiveExempt).toCteReceiveExemptResponseDto()
        return ResponseEntity.ok().body(cteReceiveExemptResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteReceiveExemptService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteReceiveExemptService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}