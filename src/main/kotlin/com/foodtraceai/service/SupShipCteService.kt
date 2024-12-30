// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.SupShipCte
import com.foodtraceai.repository.SupShipCteRepository
import com.foodtraceai.util.EntityException
import com.foodtraceai.util.SupCteStatus
import org.springframework.stereotype.Service

@Service
class SupShipCteService(
    private val supShipCteRepository: SupShipCteRepository
) : BaseService<SupShipCte>(supShipCteRepository, "SupShipCte") {

    fun findAll(fsmaUser: FsmaUser): List<SupShipCte> {
        val locationId = if (fsmaUser.isRootAdmin()) null else fsmaUser.location.id
        return supShipCteRepository.findAll(locationId)
    }

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
}
