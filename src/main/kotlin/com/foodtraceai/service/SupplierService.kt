// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.supplier.SupShipCte
import com.foodtraceai.repository.supplier.SupShipCteRepository
import com.foodtraceai.service.cte.CteReceiveService
import com.foodtraceai.service.supplier.SupShipCteService
import com.foodtraceai.util.EntityException
import com.foodtraceai.util.SupCteStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime

/*
Business Logic to receive a shipment associated with a previously
sent CteShip cte
 */

@Service
class SupplierService(
    private val supShipCteRepository: SupShipCteRepository,
    private val cteReceiveService: CteReceiveService,
    private val supShipCteService: SupShipCteService,
) {
    // RFE or Restaurant has received a shipment
    fun findSupShipCte(
        sscc: String,
        tlcId: Long,
        shipToLocationId: Long,
        supCteStatus: SupCteStatus,
    ): SupShipCte? {
        val supShipCteList = supShipCteRepository
            .findAllBySsccAndTlcIdAndShipToLocationIdAndSupCteStatus(
                sscc = sscc,
                tlcId = tlcId,
                shipToLocationId = shipToLocationId,
                supCteStatus = supCteStatus,
            )

        return when (supShipCteList.size) {
            0 -> null
            1 -> supShipCteList[0]
            else -> throw EntityException("SupShipCteList size error: ${supShipCteList.size}")
        }
    }

    fun makeReceiveCteFromSupShipCte(
        sscc: String,
        tlcId: Long,
        receiveLocationId: Long,
        receiveDate: LocalDate,
        receiveTime: OffsetDateTime,
    ): CteReceive {
        val supShipCte = findSupShipCte(
            sscc = sscc,
            tlcId = tlcId,
            shipToLocationId = receiveLocationId,
            supCteStatus = SupCteStatus.Pending,
        ) ?: throw EntityNotFoundException(
            "supShipCte not found for " +
                    "sscc: $sscc, " +
                    "tlcId: $tlcId, " +
                    "shipToLocationId: $receiveLocationId, " +
                    "supCteStatus: '${SupCteStatus.Pending}'"
        )

        val cteReceive = cteReceiveService.insert(
            CteReceive(
                location = supShipCte.shipToLocation,
                ftlItem = supShipCte.ftlItem,
                variety = supShipCte.variety,
                tlc = supShipCte.tlc,
                quantity = supShipCte.quantity,
                unitOfMeasure = supShipCte.unitOfMeasure,
                prodDesc = supShipCte.prodDesc,
                ipsLocation = supShipCte.shipFromLocation,
                receiveDate = receiveDate,
                receiveTime = receiveTime,
                tlcSource = supShipCte.tlcSource,
                tlcSourceReference = supShipCte.tlcSourceReference,
                referenceDocumentType = supShipCte.referenceDocumentType,
                referenceDocumentNum = supShipCte.referenceDocumentNum,
            )
        )

        supShipCteService.update(
            supShipCte.copy(
                cteReceive = cteReceive,
                supCteStatus = SupCteStatus.Received
            )
        )

        return cteReceive
    }
}
