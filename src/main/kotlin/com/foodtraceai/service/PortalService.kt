// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.response.ArrivingShipmentsResponseDto
import com.foodtraceai.model.response.ReceivedShipmentsResponseDto
import com.foodtraceai.model.response.toArrivingShipmentsResponseDto
import com.foodtraceai.model.response.toReceivedShipmentsResponseDto
import com.foodtraceai.repository.SupShipCteRepository
import com.foodtraceai.repository.cte.CteReceiveRepository
import com.foodtraceai.util.SupCteStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PortalService(
    private val cteReceiveRepository: CteReceiveRepository,
    private val supShipCteRepository: SupShipCteRepository
) {

    fun arrivingShipments(
        shipToLocationId: Long,
    ): List<ArrivingShipmentsResponseDto> {
        val supShipCteList = supShipCteRepository.findArrivingShipments(
            locationId = shipToLocationId,
            supCteStatus = SupCteStatus.Pending.name,
        )
        return supShipCteList.map { it.toArrivingShipmentsResponseDto() }
    }

    fun receivedShipments(
        locationId: Long,
        ipsLocationId: Long? = null,
        dayFrom: LocalDate? = null,
        dayTo: LocalDate? = null,
    ): List<ReceivedShipmentsResponseDto> {
        val cteReceiveList = cteReceiveRepository.findAllByOptionalParams(
            tlcVal = null,
            locationId = locationId,
            dayFrom = dayFrom,
            dayTo = dayTo,
        )
        return cteReceiveList.map{it.toReceivedShipmentsResponseDto()}
    }
}
