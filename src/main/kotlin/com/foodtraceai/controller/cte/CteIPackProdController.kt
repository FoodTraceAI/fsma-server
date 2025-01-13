// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteIPackProdDto
import com.foodtraceai.model.cte.toCteIPackProd
import com.foodtraceai.model.cte.toCteIPackProdDto
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.UnauthorizedRequestException
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
    ): ResponseEntity<CteIPackProdDto> {
        val cteIPackProd = cteIPackProdService.findById(id)
            ?: throw EntityNotFoundException("CteIPackProd not found = $id")
//        assertResellerClientMatchesToken(fsaUser, address.resellerId)
        return ResponseEntity.ok(cteIPackProd.toCteIPackProdDto())
    }

    // -- Create a new Address
    @PostMapping
    fun create(
        @Valid @RequestBody cteIPackProdDto: CteIPackProdDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackProdDto> {
        val location = getLocation(cteIPackProdDto.locationId, fsmaUser)
        val harvestLocation = getLocation(cteIPackProdDto.harvestLocationId, fsmaUser)

        val harvestBusiness = getFoodBus(cteIPackProdDto.harvestBusinessId, fsmaUser)

        val coolLocation = cteIPackProdDto.coolLocationId?.let { getLocation(it, fsmaUser) }

        val packTlc = getTraceLotCode(cteIPackProdDto.packTlcId, fsmaUser)

        val packTlcSource = cteIPackProdDto.packTlcSourceId?.let {
            getLocation(it, fsmaUser)
        }

        val cteIPackProd = cteIPackProdDto.toCteIPackProd(
            location, harvestLocation, harvestBusiness,
            coolLocation, packTlc, packTlcSource
        )
        val cteIPackProdDtoResponse = cteIPackProdService.insert(cteIPackProd).toCteIPackProdDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_PROD_BASE_URL.plus("/${cteIPackProdDto.id}")))
            .body(cteIPackProdDtoResponse)
    }

    // -- Update an existing CteIPackProdDto
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackProdDto: CteIPackProdDto,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<CteIPackProdDto> {
        if (cteIPackProdDto.id <= 0L || cteIPackProdDto.id != id)
            throw UnauthorizedRequestException("Conflicting CteIPackProd Ids specified: $id != ${cteIPackProdDto.id}")

        val location = getLocation(cteIPackProdDto.locationId, fsmaUser)
        val harvestLocation = getLocation(cteIPackProdDto.harvestLocationId, fsmaUser)

        val harvestBusiness = getFoodBus(cteIPackProdDto.harvestBusinessId, fsmaUser)
        val coolLocation = cteIPackProdDto.coolLocationId?.let { getLocation(it, fsmaUser) }

        val packTlc = getTraceLotCode(cteIPackProdDto.packTlcId, fsmaUser)
        val packTlcSource = cteIPackProdDto.packTlcSourceId?.let { getLocation(it, fsmaUser) }

        val cteIPackProd = cteIPackProdDto.toCteIPackProd(
            location, harvestLocation, harvestBusiness,
            coolLocation, packTlc, packTlcSource
        )
        val cteIPackProdCto = cteIPackProdService.update(cteIPackProd).toCteIPackProdDto()
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