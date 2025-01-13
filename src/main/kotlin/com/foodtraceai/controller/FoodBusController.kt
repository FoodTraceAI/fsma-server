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
    ): ResponseEntity<FoodBusDto> {
        val foodBus = getFoodBus(id, fsmaUser)
        return ResponseEntity.ok(foodBus.toFoodBusDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody foodBusDto: FoodBusDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FoodBusDto> {
        var reseller: Reseller? = null
        foodBusDto.resellerId?.let { reseller = getReseller(it, fsmaUser) }

        val mainAddress = getAddress(foodBusDto.mainAddressId, fsmaUser)
        val contact = getContact(foodBusDto.foodBusContactId, fsmaUser)
        val franchisor = foodBusDto.franchisorId?.let{ getFranchisor(it,fsmaUser)}
        val foodBus = foodBusDto.toFoodBus(reseller, mainAddress, contact, franchisor)
        val foodBusResponse = foodBusService.insert(foodBus).toFoodBusDto()
        return ResponseEntity.created(URI.create(FOOD_BUS_BASE_URL.plus("/${foodBusResponse.id}")))
            .body(foodBusResponse)
    }

    // -- Update an existing business
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody foodBusDto: FoodBusDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FoodBusDto> {
        if (foodBusDto.id <= 0L || foodBusDto.id != id)
            throw UnauthorizedRequestException("Conflicting BusinessIds specified: $id != ${foodBusDto.id}")
        val reseller: Reseller? = foodBusDto.resellerId?.let { getReseller(it, fsmaUser) }
        val mainAddress = getAddress(foodBusDto.mainAddressId, fsmaUser)
        val foodBusContact = getContact(foodBusDto.foodBusContactId, fsmaUser)
        val franchisor = foodBusDto.franchisorId?.let { getFranchisor(it,fsmaUser) }
        val foodBus = foodBusDto.toFoodBus(reseller, mainAddress, foodBusContact, franchisor)
        val foodBusResponse = foodBusService.update(foodBus)
        return ResponseEntity.ok().body(foodBusResponse.toFoodBusDto())
    }

    // -- Delete an existing business
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        foodBusService.findById(id)?.let { business ->
//            assertResellerClientMatchesToken(fsaUser, business.resellerId)
            foodBusService.delete(business) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}