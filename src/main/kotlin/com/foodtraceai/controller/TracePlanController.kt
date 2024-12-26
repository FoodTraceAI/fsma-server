package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.TracePlanDto
import com.foodtraceai.model.toTracePlan
import com.foodtraceai.model.toTracePlanDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val TRACE_PLAN_URL = "/api/v1/traceplan"
private const val TRACE_PLAN_ALT_URL = "/api/v1/trace-plan"

@RestController
@RequestMapping(TRACE_PLAN_URL,TRACE_PLAN_ALT_URL)
@SecurityRequirement(name = "bearerAuth")
class TracePlanController : BaseController() {

    // -- Return a specific TraceabilityPlan
    // -    http://localhost:8080/api/v1/traceabilityplan/10
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TracePlanDto> {
        val tracePlan = tracePlanService.findById(id)
            ?: throw EntityNotFoundException("TracePlan not found = $id")
//        assertResellerClientMatchesToken(fsaUser, tracePlan.resellerId)
        return ResponseEntity.ok(tracePlan.toTracePlanDto())
    }

    // -- Create a new TracePlan
    @PostMapping
    fun create(
        @Valid @RequestBody tracePlanDto: TracePlanDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TracePlanDto> {
        val location = getLocation(tracePlanDto.locationId,fsmaUser)
        val contact = getContact(tracePlanDto.tracePlanContactId, fsmaUser)
        val tracePlan = tracePlanDto.toTracePlan(location,contact)
        val tracePlanResponse = tracePlanService.insert(tracePlan).toTracePlanDto()
        return ResponseEntity.created(URI.create(TRACE_PLAN_URL.plus("/${tracePlanResponse.id}")))
            .body(tracePlanResponse)
    }

    // -- Update an existing TracePlan
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody tracePlanDto: TracePlanDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<TracePlanDto> {
        if (tracePlanDto.id <= 0L || tracePlanDto.id != id)
            throw UnauthorizedRequestException("Conflicting TracePlanIds specified: $id != ${tracePlanDto.id}")
        val location = getLocation(tracePlanDto.locationId,fsmaUser)
        val contact = getContact(tracePlanDto.tracePlanContactId,fsmaUser)
        val tracePlan = tracePlanDto.toTracePlan(location,contact)
        val tracePlanResponse = tracePlanService.update(tracePlan).toTracePlanDto()
        return ResponseEntity.ok().body(tracePlanResponse)
    }

    // -- Delete an existing TracePlan
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        tracePlanService.findById(id)?.let { tracePlan ->
//            assertResellerClientMatchesToken(fsaUser, tracePlan.resellerId)
            tracePlanService.delete(tracePlan) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}