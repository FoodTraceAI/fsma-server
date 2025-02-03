// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.SupShipCte
import com.foodtraceai.model.response.ArrivingShipmentsResponseDto
import com.foodtraceai.model.response.toArrivingShipmentsResponseDto
import com.foodtraceai.repository.SupShipCteRepository
import com.foodtraceai.util.SupCteStatus
import org.springframework.stereotype.Service

@Service
class PortalService(
    private val supShipCteRepository: SupShipCteRepository
) : BaseService<SupShipCte>(supShipCteRepository, "SupShipCte") {

    fun arrivingShipments(
        shipToLocationId: Long,
        supCteStatus: SupCteStatus = SupCteStatus.Pending,
    ): List<ArrivingShipmentsResponseDto> {
        val supShipCteList = supShipCteRepository.findArrivingShipments(
            locationId = shipToLocationId,
            supCteStatus = supCteStatus.name,
        )
        return supShipCteList.map { it.toArrivingShipmentsResponseDto() }
    }
}
