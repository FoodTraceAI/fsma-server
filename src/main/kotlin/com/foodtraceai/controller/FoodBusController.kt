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

private const val FOOD_BUS_BASE_URL = "/api/v1/foodbus"
private const val FOOD_BUS_ALT_BASE_URL = "/api/v1/food-bus"

@RestController
@RequestMapping(value = [FOOD_BUS_BASE_URL, FOOD_BUS_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class FoodBusController : BaseController() {

    // -- Return a specific foodBusiness
    // -    http://localhost:8080/api/v1/foodbus/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FoodBusResponseDto> {
        val foodBus = getFoodBus(id, fsmaUser)
        return ResponseEntity.ok(foodBus.toFoodBusResponseDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody foodBusRequestDto: FoodBusRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FoodBusResponseDto> {
        val reseller: Reseller? = foodBusRequestDto.resellerId?.let { getReseller(it, fsmaUser) }
        val mainAddress = getAddress(foodBusRequestDto.mainAddressId, fsmaUser)
        val contact = getContact(foodBusRequestDto.foodBusContactId, fsmaUser)
        val franchisor = foodBusRequestDto.franchisorId?.let { getFranchisor(it, fsmaUser) }
        val foodBus = foodBusRequestDto.toFoodBus(id=0,reseller, mainAddress, contact, franchisor)
        val foodBusResponse = foodBusService.insert(foodBus)
        return ResponseEntity
            .created(URI.create(FOOD_BUS_BASE_URL.plus("/${foodBusResponse.id}")))
            .body(foodBusResponse.toFoodBusResponseDto())
    }

    // -- Update an existing business
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody foodBusRequestDto: FoodBusRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FoodBusResponseDto> {
       assertFsmaUserFoodBusMatches(id, fsmaUser)
        val reseller: Reseller? = foodBusRequestDto.resellerId?.let { getReseller(it, fsmaUser) }
        val mainAddress = getAddress(foodBusRequestDto.mainAddressId, fsmaUser)
        val foodBusContact = getContact(foodBusRequestDto.foodBusContactId, fsmaUser)
        val franchisor = foodBusRequestDto.franchisorId?.let { getFranchisor(it, fsmaUser) }
        val foodBus = foodBusRequestDto.toFoodBus(id, reseller, mainAddress, foodBusContact, franchisor)
        val foodBusResponse = foodBusService.update(foodBus)
        return ResponseEntity.ok().body(foodBusResponse.toFoodBusResponseDto())
    }

    // -- Delete an existing business
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        getFoodBus(id, fsmaUser).let { foodBus ->
            foodBusService.delete(foodBus) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}