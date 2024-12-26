// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.repository.cte.CteReceiveRepository
import com.foodtraceai.service.BaseService
import com.foodtraceai.service.supplier.SupShipCteService
import com.foodtraceai.util.SupCteStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime

@Service
class CteReceiveService(
    private val cteReceiveRepository: CteReceiveRepository,
    private val supShipCteService: SupShipCteService,
) : BaseService<CteReceive>(cteReceiveRepository, "CteReceive") {

    fun findAll(fsmaUser: FsmaUser): List<CteReceive> {
        return cteReceiveRepository.findAll(
            locationId = if(fsmaUser.isRootAdmin()) null else fsmaUser.location.id,
        )
    }

    fun findAllByOptionalParams(
        tlcVal: String? = null,
        locationId: Long? = null,
        ipsLocationId: Long? = null,
        dayFrom: LocalDate? = null,
        dayTo: LocalDate? = null,
    ): List<CteReceive> {
        return cteReceiveRepository.findAllByOptionalParams(
            tlcVal,
            locationId,
            ipsLocationId,
            dayFrom,
            dayTo,
        )
    }

    fun makeReceiveCteFromSupShipCte(
        sscc: String,
        tlcId: Long,
        receiveLocationId: Long,
        receiveDate: LocalDate,
        receiveTime: OffsetDateTime,
    ): CteReceive {
        val supShipCte = supShipCteService.findSupShipCte(
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

        val cteReceive = insert(
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
