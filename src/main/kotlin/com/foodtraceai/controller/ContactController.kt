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

private const val CONTACT_BASE_URL = "/api/v1/contact"

@RestController
@RequestMapping(value = [CONTACT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class ContactController : BaseController() {

    // Return a specific contact
    // http://localhost:8080/api/v1/contac/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ContactResponseDto> {
        val contact = getContact(id, fsmaUser)
        return ResponseEntity.ok(contact.toContactResponseDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody contactRequestDto: ContactRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ContactResponseDto> {
        val contact = contactRequestDto.toContact(id = 0)
        val contactResponseDto = contactService.insert(contact).toContactResponseDto()
        return ResponseEntity
            .created(URI.create(CONTACT_BASE_URL.plus("/${contactResponseDto.id}")))
            .body(contactResponseDto)
    }

    // -- Update an existing business
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody contactRequestDto: ContactRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ContactResponseDto> {
        val contact = contactRequestDto.toContact(id)
        val contactResponse = contactService.update(contact)
        return ResponseEntity.ok().body(contactResponse.toContactResponseDto())
    }

    // -- Delete an existing business
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        getContact(id, fsmaUser).let { contact ->
            contactService.delete(contact) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}