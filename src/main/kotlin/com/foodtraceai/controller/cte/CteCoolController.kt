// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteCoolRequestDto
import com.foodtraceai.model.cte.CteCoolResponseDto
import com.foodtraceai.model.cte.toCteCool
import com.foodtraceai.model.cte.toCteCoolResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_COOL_BASE_URL = "/api/v1/cte/cool"

@RestController
@RequestMapping(value = [CTE_COOL_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteCoolController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/cool/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteCoolResponseDto> {
        val cteCool = cteCoolService.findById(id)
            ?: throw EntityNotFoundException("CteCool not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteCool.toCteCoolResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteCoolRequestDto: CteCoolRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteCoolResponseDto> {
        val location = getLocation(cteCoolRequestDto.locationId, fsmaUser)
        val isrLocation = getLocation(cteCoolRequestDto.isrLocationId, fsmaUser)
        val coolLocation = getLocation(cteCoolRequestDto.coolLocationId, fsmaUser)
        val harvestLocation = getLocation(cteCoolRequestDto.harvestLocationId, fsmaUser)
        val cteCool = cteCoolRequestDto.toCteCool(
            id = 0, location, isrLocation, coolLocation, harvestLocation
        )
        val cteCoolResponse = cteCoolService.insert(cteCool).toCteCoolResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_COOL_BASE_URL.plus("/${cteCoolResponse.id}")))
            .body(cteCoolResponse)
    }

    // -- Update an existing CteCoolDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteCoolRequestDto: CteCoolRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteCoolResponseDto> {
        val location = getLocation(cteCoolRequestDto.locationId, fsmaUser)
        val isrLocation = getLocation(cteCoolRequestDto.isrLocationId, fsmaUser)
        val coolLocation = getLocation(cteCoolRequestDto.coolLocationId, fsmaUser)
        val harvestLocation = getLocation(cteCoolRequestDto.harvestLocationId, fsmaUser)
        val cteCool = cteCoolRequestDto.toCteCool(
            id = 81, location, isrLocation, coolLocation, harvestLocation
        )
        val cteCoolResponse = cteCoolService.update(cteCool).toCteCoolResponseDto()
        return ResponseEntity.ok().body(cteCoolResponse)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteCoolService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteCoolService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}