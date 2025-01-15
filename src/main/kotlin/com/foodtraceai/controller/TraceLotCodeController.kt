// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.*
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate

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
    ): ResponseEntity<TraceLotCodeDto> {
        val traceLotCode = getTraceLotCode(id, fsmaUser)
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(traceLotCode.toTraceLotCodeDto())
    }

    // -- Create a new TraceLotCode
    @PostMapping
    fun create(
        @Valid @RequestBody traceLotCodeDto: TraceLotCodeDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TraceLotCodeDto> {
        val traceLotCode = traceLotCodeDto.toTraceLotCode(
            tlcSource = getLocation(traceLotCodeDto.tlcSourceId)
        )
        val traceLotCodeResponse = traceLotCodeService.insert(traceLotCode).toTraceLotCodeDto()
        return ResponseEntity.created(URI.create(TRACE_LOT_CODE_BASE_URL.plus("/${traceLotCodeResponse.id}")))
            .body(traceLotCodeResponse)
    }

    // -- Update an existing TraceLotCode
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody traceLotCodeDto: TraceLotCodeDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TraceLotCodeDto> {
        if (traceLotCodeDto.id <= 0L || traceLotCodeDto.id != id)
            throw UnauthorizedRequestException("Conflicting TraceLotCodeIds specified: $id != ${traceLotCodeDto.id}")
        val traceLotCode = traceLotCodeDto.toTraceLotCode(
            tlcSource = getLocation(traceLotCodeDto.tlcSourceId)
        )
        val traceLotCodeResponseDto = traceLotCodeService.update(traceLotCode).toTraceLotCodeDto()
        return ResponseEntity.ok().body(traceLotCodeResponseDto)
    }

    // -- Delete an existing TraceLotCode
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        traceLotCodeService.findById(id)?.let { traceLotCode ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            traceLotCodeService.delete(traceLotCode) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }

    data class TraceLotCodeArgs(
        val tlcVal: String,

        // PTI Recommended
        val gtin: String? = null,
        val batchLot: String? = null,

        // Optional
        val sscc: String? = null,   // AI(00) - Pallet Serial Shipping Container Code
        val packDate: LocalDate? = null,    // AI(13)
        val harvestDate: LocalDate? = null, // AI(13)
        val bestByDate: LocalDate? = null,  // AI(15)
        val logSerialNo: String? = null, // AI(21) - Logistics Serial Number

        // Extra parameters that seem to belong to the TLC
        val tlcSourceId: Long,  // Location where this TLC was assigned
        val tlcSourceReference: String? = null,
    )

    @PostMapping("/makeTraceLotCode")
    private fun makeTraceLotCode(
        @Valid @RequestBody traceLotCodeArgs: TraceLotCodeArgs,
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<TraceLotCodeDto> {
        val traceLotCodeResponse = traceLotCodeService.insert(
            TraceLotCode(
                tlcVal = traceLotCodeArgs.tlcVal,
                gtin = traceLotCodeArgs.gtin,
                batchLot = traceLotCodeArgs.batchLot,
                sscc = traceLotCodeArgs.sscc,
                packDate = traceLotCodeArgs.packDate,
                harvestDate = traceLotCodeArgs.harvestDate,
                bestByDate = traceLotCodeArgs.bestByDate,
                logSerialNo = traceLotCodeArgs.logSerialNo,
                tlcSource = getLocation(traceLotCodeArgs.tlcSourceId),
                tlcSourceReference = traceLotCodeArgs.tlcSourceReference,
            )
        )
        return ResponseEntity.created(URI.create(TRACE_LOT_CODE_BASE_URL.plus("/${traceLotCodeResponse.id}")))
            .body(traceLotCodeResponse.toTraceLotCodeDto())
    }
}