// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.TracePlan
import com.foodtraceai.repository.TracePlanRepository
import org.springframework.stereotype.Service

@Service
class TracePlanService(
    private val tracePlanRepository: TracePlanRepository
) : BaseService<TracePlan>(tracePlanRepository, "TracePlan") {

    fun findAllByLocationId(
        locationId: Long?,
    ): List<TracePlan> {
        return tracePlanRepository.findAllByLocationId(locationId)
    }
}
