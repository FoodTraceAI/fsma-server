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
    ): ResponseEntity<FsmaUserResponseDto> {
        val fsmaUs = getFsmaUser(id, fsmaUser)
        return ResponseEntity.ok(fsmaUs.toFsmaUserResponseDto())
    }

    // -- Create a new FsmaUser
    @PostMapping
    fun create(
        @Valid @RequestBody fsmaUsRequestDto: FsmaUserRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FsmaUserResponseDto> {
        val foodBus = getFoodBus(fsmaUsRequestDto.foodBusId, fsmaUser)
        val location = getLocation(fsmaUsRequestDto.locationId, fsmaUser)
        val fsmaUs = fsmaUsRequestDto.toFsmaUser(id = 0, foodBus, location)
        val fsmaUserResponseDto = fsmaUserService.insert(fsmaUs).toFsmaUserResponseDto()
        return ResponseEntity
            .created(URI.create(FSMA_USER_BASE_URL.plus("/${fsmaUserResponseDto.id}")))
            .body(fsmaUserResponseDto)
    }

    // -- Update an existing FsmaUser
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody fsmaUserRequestDto: FsmaUserRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<FsmaUserResponseDto> {
        val foodBus = getFoodBus(fsmaUserRequestDto.foodBusId, fsmaUser)
        val location = getLocation(fsmaUserRequestDto.locationId, fsmaUser)
        val fsmaUs = fsmaUserRequestDto.toFsmaUser(id, foodBus, location)
        val fsmaUserResponseDto = fsmaUserService.update(fsmaUs).toFsmaUserResponseDto()
        return ResponseEntity.ok().body(fsmaUserResponseDto)
    }

    // -- Delete an existing FsmaUser
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        getFsmaUser(id, fsmaUser).let { fsmaUs ->
            fsmaUserService.delete(fsmaUs) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}