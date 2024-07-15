// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.FsmaUserDto
import com.foodtraceai.model.toFsmaUser
import com.foodtraceai.model.toFsmaUserDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val FSMA_USER_BASE_URL = "/api/v1/fsauser"
private const val FSMA_USER_ALT_BASE_URL = "/api/v1/fsa_user"

@RestController
@RequestMapping(value = [FSMA_USER_BASE_URL, FSMA_USER_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class FsmaUserController : BaseController() {

    // -- Return a specific FsmaUser
    // -    http://localhost:8080/api/v1/fsmaUser/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FsmaUserDto> {
        val fsma = fsmaUserService.findById(id)
            ?: throw EntityNotFoundException("FsmaUser not found = $id")
        return ResponseEntity.ok(fsma.toFsmaUserDto())
    }

    // -- Create a new FsmaUser
    @PostMapping
    fun create(
        @Valid @RequestBody fsmaUserDto: FsmaUserDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FsmaUserDto> {
        val foodBus = foodBusService.findById(fsmaUserDto.foodBusId)
            ?: throw EntityNotFoundException("FoodBus not found: ${fsmaUserDto.foodBusId}")

        val location = locationService.findById(fsmaUserDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${fsmaUserDto.locationId}")

        val fsmaUser = fsmaUserDto.toFsmaUser(foodBus, location)
        val fsmaUserResponse = fsmaUserService.insert(fsmaUser).toFsmaUserDto()
        return ResponseEntity.created(URI.create(FSMA_USER_BASE_URL.plus("/${fsmaUserResponse.id}")))
            .body(fsmaUserResponse)
    }

    // -- Update an existing FsmaUser
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody fsmaUserDto: FsmaUserDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<FsmaUserDto> {
        if (fsmaUserDto.id <= 0L || fsmaUserDto.id != id)
            throw UnauthorizedRequestException("Conflicting FsmaUserIds specified: $id != ${fsmaUserDto.id}")

        val foodBus = foodBusService.findById(fsmaUserDto.foodBusId)
            ?: throw EntityNotFoundException("FoodBusiness not found: ${fsmaUserDto.foodBusId}")

        val location = locationService.findById(fsmaUserDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${fsmaUserDto.locationId}")

        val fsma = fsmaUserDto.toFsmaUser(foodBus, location)
        val fsmaUserResponse = fsmaUserService.update(fsma).toFsmaUserDto()
        return ResponseEntity.ok().body(fsmaUserResponse)
    }

    // -- Delete an existing FsmaUser
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        fsmaUserService.findById(id)?.let { fsmaUser ->
            fsmaUserService.delete(fsmaUser) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}