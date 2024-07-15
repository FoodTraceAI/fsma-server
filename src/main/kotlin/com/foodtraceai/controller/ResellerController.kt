// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.ResellerDto
import com.foodtraceai.model.toReseller
import com.foodtraceai.model.toResellerDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
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
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<ResellerDto> {
        val reseller = resellerService.findById(id)
            ?: throw EntityNotFoundException("Reseller not found = $id")
//        assertResellerClientMatchesToken(fsaUser, business.resellerId)
        return ResponseEntity.ok(reseller.toResellerDto())
    }

    // -- Create a new business
    @PostMapping
    fun create(
        @Valid @RequestBody resellerDto: ResellerDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ResellerDto> {
        val reseller = resellerDto.toReseller(fsmaUser.resellerId)
        val resellerResponse = resellerService.insert(reseller).toResellerDto()
        return ResponseEntity.created(URI.create(RESELLER_BASE_URL.plus("/${resellerResponse.id}")))
            .body(resellerResponse)
    }

    // -- Update an existing business
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody resellerDto: ResellerDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<ResellerDto> {
        if (resellerDto.id <= 0L || resellerDto.id != id)
            throw UnauthorizedRequestException("Conflicting ResellerDtos specified: $id != ${resellerDto.id}")
        val reseller = resellerDto.toReseller(fsmaUser.resellerId)
        val resellerResponse = resellerService.update(reseller)
        return ResponseEntity.ok().body(resellerResponse.toResellerDto())
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