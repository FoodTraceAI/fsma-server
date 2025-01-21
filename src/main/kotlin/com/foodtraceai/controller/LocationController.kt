// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.LocationDto
import com.foodtraceai.model.toLocation
import com.foodtraceai.model.toLocationDto
import com.foodtraceai.util.UnauthorizedRequestException
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
    ): ResponseEntity<LocationDto> {
        val location = getLocation(id, fsmaUser)
        assertFsmaUserFoodBusMatches(location.foodBus.id, fsmaUser)
        return ResponseEntity.ok(location.toLocationDto())
    }

    // -- Create a new Location
    @PostMapping
    fun create(
        @Valid @RequestBody locationDto: LocationDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<LocationDto> {
        val foodBus = getFoodBus(locationDto.foodBusId, fsmaUser)
        assertFsmaUserFoodBusMatches(foodBus.id, fsmaUser)
        val contact = getContact(locationDto.locationContactId, fsmaUser)
        val serviceAddress = getAddress(locationDto.addressId, fsmaUser)
        val location = locationDto.toLocation(foodBus, contact, serviceAddress)
        val locationResponse = locationService.insert(location)
        return ResponseEntity
            .created(URI.create(LOCATION_BASE_URL.plus("/${locationResponse.id}")))
            .body(locationResponse.toLocationDto())
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody locationDto: LocationDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<LocationDto> {
        if (locationDto.id <= 0L || locationDto.id != id)
            throw UnauthorizedRequestException("Conflicting LocationIds specified: $id != ${locationDto.id}")
        assertFsmaUserLocationMatches(locationDto.id, fsmaUser)

        val foodBus = getFoodBus(locationDto.foodBusId, fsmaUser)
        val contact = getContact(locationDto.locationContactId, fsmaUser)
        val serviceAddress = getAddress(locationDto.addressId, fsmaUser)

        val location = locationDto.toLocation(foodBus, contact, serviceAddress)
        val locationResponse = locationService.update(location)
        return ResponseEntity.ok().body(locationResponse.toLocationDto())
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