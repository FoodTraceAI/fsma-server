// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteIPackSprouts
import com.foodtraceai.repository.cte.CteIPackSproutsRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class CteIPackSproutsService(
    cteIPackSprouts: CteIPackSproutsRepository
) : BaseService<CteIPackSprouts>(cteIPackSprouts, "CteIPackSprouts")
