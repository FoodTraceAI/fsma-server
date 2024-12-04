// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.*
import com.foodtraceai.util.EntityNotFoundException
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
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FoodBusDto> {
        val foodBus = foodBusService.findById(id)
            ?: throw EntityNotFoundException("FoodBusiness not found: $id")
//        assertResellerClientMatchesToken(fsaUser, business.resellerId)
        return ResponseEntity.ok(foodBus.toFoodBusDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody foodBusDto: FoodBusDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FoodBusDto> {
        var reseller: Reseller? = null
        foodBusDto.resellerId?.let {
            reseller = resellerService.findById(it)
                ?: throw EntityNotFoundException("Reseller not found: ${foodBusDto.resellerId}")
        }

        val mainAddress = addressService.findById(foodBusDto.mainAddressId)
            ?: throw EntityNotFoundException("Address not found: ${foodBusDto.mainAddressId}")

        val contact = contactService.findById(foodBusDto.foodBusContactId)
            ?: throw EntityNotFoundException("Contact not found: ${foodBusDto.foodBusContactId}")

        val franchisor = if (foodBusDto.franchisorId == null) null
        else franchisorService.findById(foodBusDto.franchisorId)
            ?: throw EntityNotFoundException("Franchisor not found: ${foodBusDto.franchisorId}")

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
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FoodBusDto> {
        if (foodBusDto.id <= 0L || foodBusDto.id != id)
            throw UnauthorizedRequestException("Conflicting BusinessIds specified: $id != ${foodBusDto.id}")

        var reseller: Reseller? = null
        foodBusDto.resellerId?.let {
            reseller = resellerService.findById(it)
                ?: throw EntityNotFoundException("Reseller not found: ${foodBusDto.resellerId}")
        }

        val mainAddress = addressService.findById(foodBusDto.mainAddressId)
            ?: throw EntityNotFoundException("Address not found: ${foodBusDto.mainAddressId}")

        val foodBusContact = contactService.findById(foodBusDto.foodBusContactId)
            ?: throw EntityNotFoundException("Contact not found: ${foodBusDto.foodBusContactId}")

        val franchisor = foodBusDto.franchisorId?.let {
            franchisorService.findById(it)
                ?: throw EntityNotFoundException("Franchisor not found: $it")
        }

        val foodBus = foodBusDto.toFoodBus(reseller, mainAddress, foodBusContact, franchisor)
        val foodBusResponse = foodBusService.update(foodBus)
        return ResponseEntity.ok().body(foodBusResponse.toFoodBusDto())
    }

    // -- Delete an existing business
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        foodBusService.findById(id)?.let { business ->
//            assertResellerClientMatchesToken(fsaUser, business.resellerId)
            foodBusService.delete(business) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}