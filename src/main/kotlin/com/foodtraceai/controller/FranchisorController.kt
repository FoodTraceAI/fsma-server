// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.FranchisorDto
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.toFranchisor
import com.foodtraceai.model.toFranchisorDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val FRANCHISOR_BASE_URL = "/api/v1/franchisor"
private const val FRANCHISOR_ALT_BASE_URL = "/api/v1/fran"

@RestController
@RequestMapping(value = [FRANCHISOR_BASE_URL, FRANCHISOR_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class FranchisorController : BaseController() {

    // -- Return a specific Franchisor
    // -    http://localhost:8080/api/v1/franchisor/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FranchisorDto> {
        val franchisor = franchisorService.findById(id)
            ?: throw EntityNotFoundException("Franchisor not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(franchisor.toFranchisorDto())
    }

    // -- Create a new Franchisor
    @PostMapping
    fun create(
        @Valid @RequestBody franchisorDto: FranchisorDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FranchisorDto> {
        val address = getAddress(franchisorDto.addressId, authPrincipal)
            ?: throw EntityNotFoundException("Franchisor Address not found: ${franchisorDto.addressId}")
        val billingAddress = franchisorDto.billingAddressId?.let { getAddress(it, authPrincipal) }
        val mainContact = getContact(franchisorDto.mainContactId, authPrincipal)
        val billingContact = franchisorDto.billingContactId?.let { getContact(it, authPrincipal)        }

        val franchisor = franchisorDto.toFranchisor(address, mainContact, billingAddress, billingContact)
        val franchisorResponse = franchisorService.insert(franchisor).toFranchisorDto()
        return ResponseEntity.created(URI.create(FRANCHISOR_BASE_URL.plus("/${franchisorResponse.id}")))
            .body(franchisorResponse)
    }

    // -- Update an existing Franchisor
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody franchisorDto: FranchisorDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FranchisorDto> {
        if (franchisorDto.id <= 0L || franchisorDto.id != id)
            throw UnauthorizedRequestException("Conflicting FranchisorIds specified: $id != ${franchisorDto.id}")

        val address = getAddress(franchisorDto.addressId, authPrincipal)
        val billingAddress = franchisorDto.billingAddressId?.let {getAddress(it,authPrincipal)}
        val mainContact = getContact(franchisorDto.mainContactId, authPrincipal)
        val billingContact = franchisorDto.billingContactId?.let { getContact(it, authPrincipal)        }

        val franchisor = franchisorDto.toFranchisor(address, mainContact, billingAddress, billingContact)
        val franchisorResponse = franchisorService.update(franchisor).toFranchisorDto()
        return ResponseEntity.ok().body(franchisorResponse)
    }

    // -- Delete an existing Franchisor
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        franchisorService.findById(id)?.let { franchisor ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            franchisorService.delete(franchisor) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}