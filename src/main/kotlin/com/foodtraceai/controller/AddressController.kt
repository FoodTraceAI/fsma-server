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

private const val ADDRESS_BASE_URL = "/api/v1/address"

@RestController
@RequestMapping(ADDRESS_BASE_URL)
@SecurityRequirement(name = "bearerAuth")
class AddressController : BaseController() {

    // -- Return a specific Address
    // -    http://localhost:8080/api/v1/address/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<AddressResponseDto> {
        val address = getAddress(id, fsmaUser)
        return ResponseEntity.ok(address.toAddressResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody addressRequestDto: AddressRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<AddressResponseDto> {
        val address = addressRequestDto.toAddress()
        val addressResponseDto = addressService.insert(address).toAddressResponseDto()
        return ResponseEntity
            .created(URI.create(ADDRESS_BASE_URL.plus("/${addressResponseDto.id}")))
            .body(addressResponseDto)
    }

    // -- Update an existing Address
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody addressRequestDto: AddressRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<AddressResponseDto> {
        val addressResponseDto = addressService.update(addressRequestDto.toAddress(id))
        return ResponseEntity.ok().body(addressResponseDto.toAddressResponseDto())
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        addressService.findById(id)?.let { address ->
            addressService.delete(address) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}