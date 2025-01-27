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

private const val RESELLER_BASE_URL = "/api/v1/reseller"

@RestController
@RequestMapping(value = [RESELLER_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class ResellerController : BaseController() {

    // -- Return a specific reselleriness
    // -    http://localhost:8080/api/v1/reseller/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ResellerResponseDto> {
        val reseller = getReseller(id, fsmaUser)
//        assertResellerClientMatchesToken(fsaUser, business.resellerId)
        return ResponseEntity.ok(reseller.toResellerResponseDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody resellerRequestDto: ResellerRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ResellerResponseDto> {
        val mainContact = getContact(resellerRequestDto.mainContactId, fsmaUser)
        val billingContact = resellerRequestDto.billingContactId?.let { getContact(it, fsmaUser) }
        val reseller = resellerRequestDto.toReseller(
            id = 0, mainContact = mainContact, billingContact = billingContact
        )
        val resellerResponse = resellerService.insert(reseller).toResellerResponseDto()
        return ResponseEntity
            .created(URI.create(RESELLER_BASE_URL.plus("/${resellerResponse.id}")))
            .body(resellerResponse)
    }

    // -- Update an existing business
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody resellerRequestDto: ResellerRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ResellerResponseDto> {
        val mainContact = getContact(resellerRequestDto.mainContactId, fsmaUser)
        val billingContact = resellerRequestDto.billingContactId?.let { getContact(it, fsmaUser) }
        val reseller = resellerRequestDto.toReseller(
            id = id, mainContact = mainContact, billingContact = billingContact
        )
        val resellerResponse = resellerService.insert(reseller).toResellerResponseDto()
        return ResponseEntity.ok().body(resellerResponse)
    }

    // -- Delete an existing business
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        resellerService.findById(id)?.let { business ->
//            assertResellerClientMatchesToken(fsaUser, business.resellerId)
            resellerService.delete(business) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}