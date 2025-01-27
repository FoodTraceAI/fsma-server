// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteIPackProdRequestDto
import com.foodtraceai.model.cte.CteIPackProdResponseDto
import com.foodtraceai.model.cte.toCteIPackProd
import com.foodtraceai.model.cte.toCteIPackProdResponseDto
import com.foodtraceai.util.EntityNotFoundException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CTE_IPACK_PROD_BASE_URL = "/api/v1/cte/ipackprod"
private const val CTE_IPACK_PROD_ALT_BASE_URL = "/api/v1/cte/ipack-prod"
private const val CTE_IPACK_PROD_ALT2_BASE_URL = "/api/v1/cte/ipack/prod"

@RestController
@RequestMapping(value = [CTE_IPACK_PROD_BASE_URL, CTE_IPACK_PROD_ALT_BASE_URL, CTE_IPACK_PROD_ALT2_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteIPackProdController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/cte/ipackprod/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackProdResponseDto> {
        val cteIPackProd = cteIPackProdService.findById(id)
            ?: throw EntityNotFoundException("CteIPackProd not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteIPackProd.toCteIPackProdResponseDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteIPackProdRequestDto: CteIPackProdRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackProdResponseDto> {
        val location = getLocation(cteIPackProdRequestDto.locationId, fsmaUser)
        val harvestLocation = getLocation(cteIPackProdRequestDto.harvestLocationId, fsmaUser)
        val harvestBusiness = getFoodBus(cteIPackProdRequestDto.harvestBusinessId, fsmaUser)
        val coolLocation = cteIPackProdRequestDto.coolLocationId?.let { getLocation(it, fsmaUser) }
        val packTlc = getTraceLotCode(cteIPackProdRequestDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackProdRequestDto.packTlcSourceId?.let {
            getLocation(it, fsmaUser)
        }
        val cteIPackProd = cteIPackProdRequestDto.toCteIPackProd(
            id = 0, location, harvestLocation, harvestBusiness, coolLocation, packTlc, packTlcSource
        )
        val cteIPackProdDtoResponse = cteIPackProdService.insert(cteIPackProd).toCteIPackProdResponseDto()
        return ResponseEntity
            .created(URI.create(CTE_IPACK_PROD_BASE_URL.plus("/${cteIPackProdDtoResponse.id}")))
            .body(cteIPackProdDtoResponse)
    }

    // -- Update an existing CteIPackProdDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackProdRequestDto: CteIPackProdRequestDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackProdResponseDto> {
        val location = getLocation(cteIPackProdRequestDto.locationId, fsmaUser)
        val harvestLocation = getLocation(cteIPackProdRequestDto.harvestLocationId, fsmaUser)
        val harvestBusiness = getFoodBus(cteIPackProdRequestDto.harvestBusinessId, fsmaUser)
        val coolLocation = cteIPackProdRequestDto.coolLocationId?.let { getLocation(it, fsmaUser) }
        val packTlc = getTraceLotCode(cteIPackProdRequestDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackProdRequestDto.packTlcSourceId?.let { getLocation(it, fsmaUser) }
        val cteIPackProd = cteIPackProdRequestDto.toCteIPackProd(
            id = id, location, harvestLocation, harvestBusiness, coolLocation, packTlc, packTlcSource
        )
        val cteIPackProdCto = cteIPackProdService.update(cteIPackProd).toCteIPackProdResponseDto()
        return ResponseEntity.ok().body(cteIPackProdCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<Void> {
        cteIPackProdService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteIPackProdService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}