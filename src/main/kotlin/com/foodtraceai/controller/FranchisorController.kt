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
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FranchisorResponseDto> {
        val franchisor = getFranchisor(id, fsmaUser)
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(franchisor.toFranchisorResponseDto())
    }

    // -- Create a new Franchisor
    @PostMapping
    fun create(
        @Valid @RequestBody franchisorRequestDto: FranchisorRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FranchisorResponseDto> {
        val address = getAddress(franchisorRequestDto.addressId, fsmaUser)
        val billingAddress = franchisorRequestDto.billingAddressId?.let { getAddress(it, fsmaUser) }
        val mainContact = getContact(franchisorRequestDto.mainContactId, fsmaUser)
        val billingContact = franchisorRequestDto.billingContactId?.let { getContact(it, fsmaUser) }
        val franchisor = franchisorRequestDto.toFranchisor(
            id = 0, address, mainContact, billingAddress, billingContact
        )
        val franchisorResponse = franchisorService.insert(franchisor).toFranchisorResponseDto()
        return ResponseEntity
            .created(URI.create(FRANCHISOR_BASE_URL.plus("/${franchisorResponse.id}")))
            .body(franchisorResponse)
    }

    // -- Update an existing Franchisor
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody franchisorRequestDto: FranchisorRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FranchisorResponseDto> {
        val address = getAddress(franchisorRequestDto.addressId, fsmaUser)
        val billingAddress = franchisorRequestDto.billingAddressId?.let { getAddress(it, fsmaUser) }
        val mainContact = getContact(franchisorRequestDto.mainContactId, fsmaUser)
        val billingContact = franchisorRequestDto.billingContactId?.let { getContact(it, fsmaUser) }
        val franchisor = franchisorRequestDto.toFranchisor(
            id = id, address, mainContact, billingAddress, billingContact
        )
        val franchisorResponse = franchisorService.update(franchisor).toFranchisorResponseDto()
        return ResponseEntity.ok().body(franchisorResponse)
    }

    // -- Delete an existing Franchisor
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        franchisorService.findById(id)?.let { franchisor ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            franchisorService.delete(franchisor) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}