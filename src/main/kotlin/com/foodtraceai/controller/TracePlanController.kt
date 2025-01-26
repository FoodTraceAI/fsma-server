package com.foodtraceai.controller

import com.foodtraceai.model.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val TRACE_PLAN_URL = "/api/v1/traceplan"
private const val TRACE_PLAN_ALT_URL = "/api/v1/trace-plan"

@RestController
@RequestMapping(TRACE_PLAN_URL, TRACE_PLAN_ALT_URL)
@SecurityRequirement(name = "bearerAuth")
class TracePlanController : BaseController() {

    // -- Return a specific TraceabilityPlan
    // -    http://localhost:8080/api/v1/traceabilityplan/10
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TracePlanResponseDto> {
        val tracePlan = getTracePlan(id, fsmaUser)
        return ResponseEntity.ok(tracePlan.toTracePlanResponseDto())
    }

    // -- Create a new TracePlan
    @PostMapping
    fun create(
        @Valid @RequestBody tracePlanRequestDto: TracePlanRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TracePlanResponseDto> {
        val location = getLocation(tracePlanRequestDto.locationId, fsmaUser)
        val contact = getContact(tracePlanRequestDto.tracePlanContactId, fsmaUser)
        val tracePlan = tracePlanRequestDto.toTracePlan(id = 0, location, contact)
        val tracePlanResponse = tracePlanService.insert(tracePlan)
        return ResponseEntity
            .created(URI.create(TRACE_PLAN_URL.plus("/${tracePlanResponse.id}")))
            .body(tracePlanResponse.toTracePlanResponseDto())
    }

    // -- Update an existing TracePlan
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody tracePlanResponseDto: TracePlanRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TracePlanResponseDto> {
        val location = getLocation(tracePlanResponseDto.locationId, fsmaUser)
        val contact = getContact(tracePlanResponseDto.tracePlanContactId, fsmaUser)
        val tracePlan = tracePlanResponseDto.toTracePlan(id=id, location, contact)
        val tracePlanResponse = tracePlanService.update(tracePlan).toTracePlanResponseDto()
        return ResponseEntity.ok().body(tracePlanResponse)
    }

    // -- Delete an existing TracePlan
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        getTracePlan(id, fsmaUser).let { tracePlan ->
            tracePlanService.delete(tracePlan) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}