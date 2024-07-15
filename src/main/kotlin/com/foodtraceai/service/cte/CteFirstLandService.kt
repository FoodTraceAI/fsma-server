// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteFirstLand
import com.foodtraceai.repository.cte.CteFirstLandRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class CteFirstLandService(
    cteFirstLandRepository: CteFirstLandRepository
) : BaseService<CteFirstLand>(cteFirstLandRepository, "CteFirstLand")
