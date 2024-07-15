// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.AddressDto
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.toAddress
import com.foodtraceai.model.toAddressDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
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
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<AddressDto> {
        val address = addressService.findById(id)
            ?: throw EntityNotFoundException("Address not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(address.toAddressDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody addressDto: AddressDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<AddressDto> {
        val address = addressDto.toAddress(fsmaUser.resellerId)
        val addressResponse = addressService.insert(address).toAddressDto()
        return ResponseEntity
            .created(URI.create(ADDRESS_BASE_URL.plus("/${addressResponse.id}")))
            .body(addressResponse)
    }

    // -- Update an existing Address
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody addressDto: AddressDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<AddressDto> {
        if (addressDto.id <= 0L || addressDto.id != id)
            throw UnauthorizedRequestException("Conflicting AddressIds specified: $id != ${addressDto.id}")
        val address = addressDto.toAddress(fsmaUser.resellerId)
        val addressResponse = addressService.update(address).toAddressDto()
        return ResponseEntity.ok().body(addressResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        addressService.findById(id)?.let { address ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            addressService.delete(address) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}