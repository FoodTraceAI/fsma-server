// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.TraceLotCode
import com.foodtraceai.repository.TraceLotCodeRepository
import org.springframework.stereotype.Service

@Service
class TraceLotCodeService(
    tlcRepository: TraceLotCodeRepository
) : BaseService<TraceLotCode>(tlcRepository, "TraceabilityLotCode")
