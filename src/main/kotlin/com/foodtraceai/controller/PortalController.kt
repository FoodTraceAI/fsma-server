// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.*
import com.foodtraceai.model.response.ArrivingShipmentsResponseDto
import com.foodtraceai.model.response.ReceivedShipmentsResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

private const val PORTAL_BASE_URL = "/api/v1/portal"

@RestController
@RequestMapping(PORTAL_BASE_URL)
@SecurityRequirement(name = "bearerAuth")
class PortalController : BaseController() {

    // localhost:8080/api/v1/portal/arrivingshipments?locationId=1
    @GetMapping("/arrivingshipments")
    fun findArrivingShipments(
        @RequestParam("locationId") locationId: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<List<ArrivingShipmentsResponseDto>> {
        val arriving = portalService.arrivingShipments(locationId)
        return ResponseEntity.ok(arriving)
    }

    // localhost:8080/api/v1/portal/receivedshipments?locationId=1
    @GetMapping("/receivedshipments")
    fun findReceivedShipments(
        @RequestParam("locationId") locationId: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<List<ReceivedShipmentsResponseDto>> {
        val received = portalService.receivedShipments(locationId)
        return ResponseEntity.ok(received)
    }

    data class PortalTracePlanResponse(
        override val id: Long,
        val locationId: Long,
        val issueDate: OffsetDateTime? = null,
        val previousTracePlanId: Long? = null,
        val traceabilityPlanUpdates: String? = null,
        val descProcRecordMaintenance: String,
        val descProcIdentifyFoods: String,
        val descAssignTraceLotCodes: String,
        val tracePlanContactId: Long,
        val farmMap: ByteArray? = null,
        override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
        override var dateModified: OffsetDateTime = OffsetDateTime.now(),
        override var isDeleted: Boolean = false,
        override var dateDeleted: OffsetDateTime? = null,
        override var authUsername: String? = null,
        val address: Address,
        val contact: Contact,
    ) : BaseResponse<PortalTracePlanResponse>()

    fun TracePlan.toPortalTracePlanResponse() = PortalTracePlanResponse(
        id = id,
        locationId = location.id,
        issueDate = issueDate,
        previousTracePlanId = previousTracePlanId,
        traceabilityPlanUpdates = traceabilityPlanUpdates,
        descProcRecordMaintenance = descProcRecordMaintenance,
        descProcIdentifyFoods = descProcIdentifyFoods,
        descAssignTraceLotCodes = descAssignTraceLotCodes,
        tracePlanContactId = tracePlanContact.id,
        farmMap = farmMap,
        dateCreated = dateCreated,
        dateModified = dateModified,
        isDeleted = isDeleted,
        dateDeleted = dateDeleted,
        authUsername = authUsername,
        address = location.address,
        contact = tracePlanContact,
    )

    // localhost:8080/api/v1/portal/traceplan?locationId=2
    @GetMapping("/traceplan")
    fun findTracePlanByLocationId(
        @RequestParam("locationId") locationId: Long?,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<PortalTracePlanResponse> {
        val tracePlan = tracePlanService.findAllByLocationId(locationId).firstOrNull()
            ?: throw EntityNotFoundException("TracePlan not found locationId: $locationId")
        return ResponseEntity.ok(tracePlan.toPortalTracePlanResponse())
    }

    // localhost:8080/api/v1/portal/traceplan/1
    @GetMapping("/traceplan/{id}")
    fun findTracePlanById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<PortalTracePlanResponse> {
        val portalTracePlan = getTracePlan(id, fsmaUser)
        return ResponseEntity.ok(portalTracePlan.toPortalTracePlanResponse())
    }
}