// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.Location
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.supplier.SupShipCte
import com.foodtraceai.service.cte.CteReceiveService
import com.foodtraceai.util.ReferenceDocumentType
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime

/*
Business Logic to receive a shipment associated with a previously
sent CteShip cte
 */

@Service
class SupplierService(
    private val cteReceiveService: CteReceiveService,
) {
    // RFE or Restaurant has received a shipment
    fun receiveSupShipment(
        supShipCte: SupShipCte,
        location: Location,
        receiveDate: LocalDate,
        referenceDocumentType: ReferenceDocumentType,
        referenceDocumentNum: String,
    ): CteReceive {
        val cteReceive = CteReceive(
            location = supShipCte.shipToLocation,
            traceLotCode = supShipCte.tlc,
            quantity = supShipCte.quantity,
            unitOfMeasure = supShipCte.unitOfMeasure,
            ftlItem = supShipCte.ftlItem,
            variety = supShipCte.variety,
            foodDesc = supShipCte.foodDesc,
            ipsLocation = supShipCte.shipFromLocation,
            receiveDate = receiveDate,
            receiveTime = OffsetDateTime.now(),
            tlcSource = supShipCte.tlcSource,
            tlcSourceReference = supShipCte.tlcSourceReference,
            referenceDocumentType = referenceDocumentType,
            referenceDocumentNum = referenceDocumentNum,
        )

        return cteReceiveService.save(cteReceive)
    }
}
