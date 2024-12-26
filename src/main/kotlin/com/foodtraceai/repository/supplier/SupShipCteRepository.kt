// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.repository.supplier

import com.foodtraceai.model.supplier.SupShipCte
import com.foodtraceai.repository.BaseRepository
import com.foodtraceai.util.SupCteStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SupShipCteRepository : BaseRepository<SupShipCte> {
    @Query(
        value = "select sup from SupShipCte sup " +
                "where (:locationId is null or :locationId = sup.shipToLocation.id) and " +
                "(sup.dateDeleted is null) " +
                "order by sup.dateCreated"
    )
    fun findAll(
        @Param("locationId") locationId: Long?,
    ): List<SupShipCte>

    fun findAllBySsccAndTlcIdAndShipToLocationIdAndSupCteStatus(
        sscc: String,
        tlcId: Long,
        shipToLocationId: Long,
        supCteStatus: SupCteStatus,
    ): List<SupShipCte>
}