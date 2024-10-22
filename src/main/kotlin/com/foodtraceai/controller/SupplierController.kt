package com.foodtraceai.controller

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.supplier.SupShipCteDto
import com.foodtraceai.model.supplier.toSupShipCteDto
import com.foodtraceai.util.Sscc
import com.foodtraceai.util.SupCteStatus
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.OffsetDateTime

private const val SUPPLIER_BASE_URL = "/api/v1/supplier"

@RestController
@RequestMapping(value = [SUPPLIER_BASE_URL])
//@SecurityRequirement(name = "bearerAuth")
class SupplierController : BaseController() {

    // http://localhost:8080/api/v1/supplier/findShipCte?sscc=sscc1&tlcId=1&shipFromLocationId=1
    @GetMapping("/findShipCte")
    private fun findShipCte(
        @RequestParam(value = "sscc", required = true) sscc: Sscc,
        @RequestParam(value = "tlcId", required = true) tlcId: Long,
        @RequestParam(value = "shipToLocationId", required = true) shipToLocationId: Long,
        @AuthenticationPrincipal fsmaUser: FsmaUser
    ): ResponseEntity<SupShipCteDto?> {
        val supShipCte = supplierService.findSupShipCte(
            sscc = sscc,
            tlcId = tlcId,
            shipToLocationId = shipToLocationId,
            supCteStatus = SupCteStatus.Pending,
        )

        return ResponseEntity.ok(supShipCte?.toSupShipCteDto())
    }

    data class ShipArgs(
        val sscc: Sscc,
        val tlcId: Long,
        val shipToLocationId: Long,
        val receiveDate: LocalDate,
        val receiveTime: OffsetDateTime,
    )

    @PostMapping("/makeReceiveCte")
    private fun makeReceiveCte(
        @Valid @RequestBody shipArgs: ShipArgs,
        @AuthenticationPrincipal fsmaUser: FsmaUser,
    ): ResponseEntity<CteReceive> {
        val cteReceive = supplierService.makeReceiveCteFromSupShipCte(
            shipArgs.sscc,
            shipArgs.tlcId,
            shipArgs.shipToLocationId,
            shipArgs.receiveDate,
            shipArgs.receiveTime,
        )
        return ResponseEntity.ok(cteReceive)
    }
}