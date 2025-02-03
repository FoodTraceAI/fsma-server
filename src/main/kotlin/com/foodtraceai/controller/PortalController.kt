// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.response.ArrivingShipmentsResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private const val PORTAL_BASE_URL = "/api/v1/portal"

@RestController
@RequestMapping(PORTAL_BASE_URL)
@SecurityRequirement(name = "bearerAuth")
class PortalController : BaseController() {

    // localhost:8080/api/v1/portal/arrivingshipments?locationId=1
    @GetMapping("/arrivingshipments")
    fun findById(
        @RequestParam("locationId") locationId: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<List<ArrivingShipmentsResponseDto>> {
        val arriving = portalService.arrivingShipments(locationId)
        return ResponseEntity.ok(arriving)
    }
}