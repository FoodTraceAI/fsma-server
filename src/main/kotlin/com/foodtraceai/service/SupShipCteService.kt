// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.SupShipCte
import com.foodtraceai.repository.SupShipCteRepository
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
    ): List<SupShipCte> {
        val supShipCteList = supShipCteRepository
            .findAllBySsccAndTlcIdAndShipToLocationIdAndSupCteStatus(
                sscc = sscc,
                tlcId = tlcId,
                shipToLocationId = shipToLocationId,
                supCteStatus = supCteStatus,
            )
        return supShipCteList
    }
}
