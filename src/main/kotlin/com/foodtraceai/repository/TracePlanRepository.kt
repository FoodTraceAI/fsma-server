// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.repository

import com.foodtraceai.model.TracePlan
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TracePlanRepository : BaseRepository<TracePlan> {
    @Query(
        value = "select tp from TracePlan tp " +
                "where (:locationId is null or :locationId = tp.location.id) and " +
                "(tp.dateDeleted is null) " +
                "order by tp.issueDate desc"
    )
    fun findAllByLocationId(
        locationId: Long?,
    ): List<TracePlan>
}