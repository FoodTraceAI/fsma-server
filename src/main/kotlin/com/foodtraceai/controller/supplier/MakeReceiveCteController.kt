package com.foodtraceai.controller.supplier

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceive
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.OffsetDateTime

private const val SUPPLIER_BASE_URL = "/api/v1/supplier"

@RestController
@RequestMapping(value = [SUPPLIER_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class MakeReceiveCteController : BaseController() {

    data class ShipArgs(
        val sscc: String,
        val tlcId: Long,
        val receiveLocationId: Long,
        val receiveDate: LocalDate,
        val receiveTime: OffsetDateTime,
    )

    @PostMapping("/makeReceiveCte")
    private fun makeReceiveCte(
        @Valid @RequestBody shipArgs: ShipArgs,
        @AuthenticationPrincipal authPrincipal: FsmaUser,
    ): ResponseEntity<CteReceive> {
        val cteReceive = supplierService.makeReceiveCteFromSupShipCte(
            shipArgs.sscc,
            shipArgs.tlcId,
            shipArgs.receiveLocationId,
            shipArgs.receiveDate,
            shipArgs.receiveTime,
        )
        return ResponseEntity.ok(cteReceive)
    }
}