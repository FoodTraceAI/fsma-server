// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.repository.supplier

import com.foodtraceai.model.supplier.SupShipCte
import com.foodtraceai.repository.BaseRepository
import com.foodtraceai.util.SupCteStatus
import org.springframework.stereotype.Repository

@Repository
interface SupShipCteRepository : BaseRepository<SupShipCte> {
    fun findAllBySsccAndTlcIdAndShipToLocationIdAndSupCteStatus(
        sscc: String,
        tlcId: Long,
        shipToLocationId:Long,
        supCteStatus: SupCteStatus,
    ):List<SupShipCte>
}