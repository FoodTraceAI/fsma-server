// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.repository

import com.foodtraceai.model.SupShipCte
import com.foodtraceai.util.SupCteStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SupShipCteRepository : BaseRepository<SupShipCte> {
    fun findAllBySsccAndTlcIdAndShipToLocationIdAndSupCteStatus(
        sscc: String,
        tlcId: Long,
        shipToLocationId: Long,
        supCteStatus: SupCteStatus,
    ): List<SupShipCte>

    @Query(
        value = "select sup from SupShipCte sup " +
                "where (:locationId is null or :locationId = sup.shipToLocation.id) and " +
                "(sup.dateDeleted is null) " +
                "order by sup.dateCreated"
    )
    fun findAll(
        @Param("locationId") locationId: Long?,
    ): List<SupShipCte>

    @Query(
        value = "select sup from SupShipCte sup " +
                "where (:locationId is null or :locationId = sup.shipToLocation.id) and " +
                "(supCteStatus is null or CAST(:supCteStatus as text) = sup.supCteStatus) and " +
                "(sup.dateDeleted is null) " +
                "order by sup.dateCreated"
    )
    fun findArrivingShipments(
        @Param("locationId") locationId: Long?,
        @Param("supCteStatus") supCteStatus: String? = null,
    ): List<SupShipCte>
}