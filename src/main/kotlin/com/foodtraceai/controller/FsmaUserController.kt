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

private const val FSMA_USER_BASE_URL = "/api/v1/fsmauser"
private const val FSMA_USER_ALT_BASE_URL = "/api/v1/fsma_user"

@RestController
@RequestMapping(value = [FSMA_USER_BASE_URL, FSMA_USER_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class FsmaUserController : BaseController() {

    // -- Return a specific FsmaUser
    // -    http://localhost:8080/api/v1/fsmauser/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FsmaUserDto> {
        val fsma = fsmaUserService.findById(id)
            ?: throw EntityNotFoundException("FsmaUser not found = $id")
        return ResponseEntity.ok(fsma.toFsmaUserDto())
    }

    // -- Create a new FsmaUser
    @PostMapping
    fun create(
        @Valid @RequestBody fsmaUserDto: FsmaUserDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FsmaUserDto> {
        val foodBus = getFoodBus(fsmaUserDto.foodBusId,fsmaUser)
        val location = getLocation(fsmaUserDto.locationId,fsmaUser)
        val newFsmaUser = fsmaUserDto.toFsmaUser(foodBus, location)
        val newFsmaUserResponse = fsmaUserService.insert(newFsmaUser).toFsmaUserDto()
        return ResponseEntity.created(URI.create(FSMA_USER_BASE_URL.plus("/${newFsmaUserResponse.id}")))
            .body(newFsmaUserResponse)
    }

    // -- Update an existing FsmaUser
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody fsmaUserDto: FsmaUserDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FsmaUserDto> {
        if (fsmaUserDto.id <= 0L || fsmaUserDto.id != id)
            throw UnauthorizedRequestException("Conflicting FsmaUserIds specified: $id != ${fsmaUserDto.id}")

        val foodBus = getFoodBus(fsmaUserDto.foodBusId,fsmaUser)
        val location = getLocation(fsmaUserDto.locationId,fsmaUser)

        val fsma = fsmaUserDto.toFsmaUser(foodBus, location)
        val fsmaUserResponse = fsmaUserService.update(fsma).toFsmaUserDto()
        return ResponseEntity.ok().body(fsmaUserResponse)
    }

    // -- Delete an existing FsmaUser
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        fsmaUserService.findById(id)?.let { fsmaUser ->
            fsmaUserService.delete(fsmaUser) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}