// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller.cte

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteHarvest
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

private const val CTE_IPACK_PROD_BASE_URL = "/api/v1/cteipackprod"
private const val CTE_IPACK_PROD_ALT_BASE_URL = "/api/v1/cte-ipack-prod"
private const val CTE_IPACK_PROD_ALT2_BASE_URL = "/api/v1/cte/ipack"

@RestController
@RequestMapping(value = [CTE_IPACK_PROD_BASE_URL, CTE_IPACK_PROD_ALT_BASE_URL, CTE_IPACK_PROD_ALT2_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class CteIPackProdController : BaseController() {

    // -- Return a specific CteCool
    // -    http://localhost:8080/api/v1/addresses/1
    @GetMapping("/{id}")
    fun findById(
        @PathVariable(value = "id") id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
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
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackProdDto> {
        val location = locationService.findById(cteIPackProdDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteIPackProdDto.locationId}")

        var cteHarvest: CteHarvest? = null
        if (cteIPackProdDto.cteHarvestId != null)
            cteHarvest = cteHarvestService.findById(cteIPackProdDto.cteHarvestId)
                ?: throw EntityNotFoundException("CteHarvest not found: ${cteIPackProdDto.cteHarvestId}")

        val harvestLocation = locationService.findById(cteIPackProdDto.harvestLocationId)
            ?: throw EntityNotFoundException("HarvestLocation not found: ${cteIPackProdDto.harvestLocationId}")

        val harvestBusiness = foodBusService.findById(cteIPackProdDto.harvestBusinessId)
            ?: throw EntityNotFoundException("HarvestBusiness not found: ${cteIPackProdDto.harvestBusinessId}")

        val coolLocation = cteIPackProdDto.coolLocationId?.let {
            locationService.findById(it) ?: throw EntityNotFoundException("CoolLocation not found: $it")
        }

        val packTlc = traceLotCodeService.findById(cteIPackProdDto.packTlcId)
            ?: throw EntityNotFoundException("PackTlc not found: ${cteIPackProdDto.packTlcId}")

        val packTlcSource = cteIPackProdDto.packTlcSourceId?.let {
            locationService.findById(it) ?: throw EntityNotFoundException("PackTlcSource not found: $it")
        }

        val cteIPackProd = cteIPackProdDto.toCteIPackProd(
            location, cteHarvest, harvestLocation, harvestBusiness,
            coolLocation, packTlc, packTlcSource
        )
        val cteIPackProdDto = cteIPackProdService.insert(cteIPackProd).toCteIPackProdDto()
        return ResponseEntity.created(URI.create(CTE_IPACK_PROD_BASE_URL.plus("/${cteIPackProdDto.id}")))
            .body(cteIPackProdDto)
    }

    // -- Update an existing Location
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody cteIPackProdDto: CteIPackProdDto,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<CteIPackProdDto> {
        if (cteIPackProdDto.id <= 0L || cteIPackProdDto.id != id)
            throw UnauthorizedRequestException("Conflicting CteIPackProd Ids specified: $id != ${cteIPackProdDto.id}")

        val location = locationService.findById(cteIPackProdDto.locationId)
            ?: throw EntityNotFoundException("Location not found: ${cteIPackProdDto.locationId}")

        var cteHarvest: CteHarvest? = null
        if (cteIPackProdDto.cteHarvestId != null)
            cteHarvest = cteHarvestService.findById(cteIPackProdDto.cteHarvestId)
                ?: throw EntityNotFoundException("CteHarvest not found: ${cteIPackProdDto.cteHarvestId}")

        val harvestLocation = locationService.findById(cteIPackProdDto.harvestLocationId)
            ?: throw EntityNotFoundException("HarvestLocation not found: ${cteIPackProdDto.harvestLocationId}")

        val harvestBusiness = foodBusService.findById(cteIPackProdDto.harvestBusinessId)
            ?: throw EntityNotFoundException("HarvestBusiness not found: ${cteIPackProdDto.harvestBusinessId}")

        val coolLocation = cteIPackProdDto.coolLocationId?.let {
            locationService.findById(it) ?: throw EntityNotFoundException("CoolLocation not found: $it")
        }

        val packTlc = traceLotCodeService.findById(cteIPackProdDto.packTlcId)
            ?: throw EntityNotFoundException("PackTlc not found: ${cteIPackProdDto.packTlcId}")

        val packTlcSource = cteIPackProdDto.packTlcSourceId?.let {
            locationService.findById(it) ?: throw EntityNotFoundException("PackTlcSource not found: $it")
        }

        val cteIPackProd = cteIPackProdDto.toCteIPackProd(
            location, cteHarvest, harvestLocation, harvestBusiness,
            coolLocation, packTlc, packTlcSource
        )
        val cteIPackProdCto = cteIPackProdService.update(cteIPackProd).toCteIPackProdDto()
        return ResponseEntity.ok().body(cteIPackProdCto)
    }

    // -- Delete an existing Address
    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @AuthenticationPrincipal authPrincipal: FsmaUser
    ): ResponseEntity<Void> {
        cteIPackProdService.findById(id)?.let { ctcCoolCto ->
//            assertResellerClientMatchesToken(fsaUser, address.resellerId)
            cteIPackProdService.delete(ctcCoolCto) // soft delete?
        }
        return ResponseEntity.noContent().build()
    }
}