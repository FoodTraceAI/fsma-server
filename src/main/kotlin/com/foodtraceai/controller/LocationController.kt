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

private const val LOCATION_BASE_URL = "/api/v1/location"
private const val LOCATION_ALT_BASE_URL = "/api/v1/loc"


@RestController
@RequestMapping(value = [LOCATION_BASE_URL, LOCATION_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class LocationController : BaseController() {

    // -- Return a specific Location
    // -    http://localhost:8080/api/v1/location/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<LocationResponseDto> {
        val location = getLocation(id, fsmaUser)
        assertFsmaUserFoodBusMatches(location.foodBus.id, fsmaUser)
        return ResponseEntity.ok(location.toLocationResponseDto())
    }

    // -- Create a new Location
    @PostMapping
    fun create(
        @Valid @RequestBody locationRequestDto: LocationRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<LocationResponseDto> {
        val foodBus = getFoodBus(locationRequestDto.foodBusId, fsmaUser)
        assertFsmaUserFoodBusMatches(foodBus.id, fsmaUser)
        val contact = getContact(locationRequestDto.locationContactId, fsmaUser)
        val serviceAddress = getAddress(locationRequestDto.addressId, fsmaUser)
        val location = locationRequestDto.toLocation(id = 0, foodBus, contact, serviceAddress)
        val locationResponse = locationService.insert(location)
        return ResponseEntity
            .created(URI.create(LOCATION_BASE_URL.plus("/${locationResponse.id}")))
            .body(locationResponse.toLocationResponseDto())
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody locationRequestDto: LocationRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<LocationResponseDto> {
        assertFsmaUserLocationMatches(id, fsmaUser)
        val foodBus = getFoodBus(locationRequestDto.foodBusId, fsmaUser)
        val contact = getContact(locationRequestDto.locationContactId, fsmaUser)
        val serviceAddress = getAddress(locationRequestDto.addressId, fsmaUser)
        val location = locationRequestDto.toLocation(id = id, foodBus, contact, serviceAddress)
        val locationResponse = locationService.update(location)
        return ResponseEntity.ok().body(locationResponse.toLocationResponseDto())
    }

    // -- Delete an existing Location
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        locationService.findById(id)?.let { location ->
            assertFsmaUserLocationMatches(location.id, fsmaUser)
            locationService.delete(location) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}