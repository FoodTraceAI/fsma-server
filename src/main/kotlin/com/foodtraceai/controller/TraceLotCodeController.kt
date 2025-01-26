// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val TRACE_LOT_CODE_BASE_URL = "/api/v1/tlc"
private const val TRACE_LOT_CODE_ALT_BASE_URL = "/api/v1/trace-lot-code"
private const val TRACE_LOT_CODE_ALT2_BASE_URL = "/api/v1/tracelotcode"

@RestController
@RequestMapping(value = [TRACE_LOT_CODE_BASE_URL, TRACE_LOT_CODE_ALT_BASE_URL, TRACE_LOT_CODE_ALT2_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class TraceLotCodeController : BaseController() {

    // -- Return a specific TraceLotCode
    // -    http://localhost:8080/api/v1/tlc/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TraceLotCodeResponseDto> {
        val traceLotCode = getTraceLotCode(id, fsmaUser)
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(traceLotCode.toTraceLotCodeResponseDto())
    }

    // -- Create a new TraceLotCode
    @PostMapping
    fun create(
        @Valid @RequestBody traceLotCodeRequestDto: TraceLotCodeRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TraceLotCodeResponseDto> {
        val tlcSource = getLocation(traceLotCodeRequestDto.tlcSourceId)
        val traceLotCode = traceLotCodeRequestDto.toTraceLotCode(
            id = 0, tlcSource = tlcSource
        )
        val traceLotCodeResponse = traceLotCodeService.insert(traceLotCode).toTraceLotCodeResponseDto()
        return ResponseEntity
            .created(URI.create(TRACE_LOT_CODE_BASE_URL.plus("/${traceLotCodeResponse.id}")))
            .body(traceLotCodeResponse)
    }

    // -- Update an existing TraceLotCode
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody traceLotCodeRequestDto: TraceLotCodeRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TraceLotCodeResponseDto> {
        val tlcSource = getLocation(traceLotCodeRequestDto.tlcSourceId)
        val traceLotCode = traceLotCodeRequestDto.toTraceLotCode(
            id = 0, tlcSource = tlcSource,
        )
        val traceLotCodeResponseDto = traceLotCodeService.update(traceLotCode).toTraceLotCodeResponseDto()
        return ResponseEntity.ok().body(traceLotCodeResponseDto)
    }

    // -- Delete an existing TraceLotCode
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        getTraceLotCode(id, fsmaUser).let { traceLotCode ->
            traceLotCodeService.delete(traceLotCode) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}