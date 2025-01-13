// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.ContactDto
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.toContact
import com.foodtraceai.model.toContactDto
import com.foodtraceai.util.UnauthorizedRequestException
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
    ): ResponseEntity<ContactDto> {
        val contact = getContact(id, fsmaUser)
        return ResponseEntity.ok(contact.toContactDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody contactDto: ContactDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ContactDto> {
        val contact = contactDto.toContact()
        val contactResponse = contactService.insert(contact).toContactDto()
        return ResponseEntity.created(URI.create(CONTACT_BASE_URL.plus("/${contactResponse.id}")))
            .body(contactResponse)
    }

    // -- Update an existing business
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody contactDto: ContactDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ContactDto> {
        if (contactDto.id <= 0L || contactDto.id != id)
            throw UnauthorizedRequestException("Conflicting ContactDtos specified: $id != ${contactDto.id}")
        val contact = contactDto.toContact()
        val contactResponse = contactService.update(contact)
        return ResponseEntity.ok().body(contactResponse.toContactDto())
    }

    // -- Delete an existing business
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        contactService.findById(id)?.let { business ->
//            assertContactClientMatchesToken(fsaUser, business.contactId)
            contactService.delete(business) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}