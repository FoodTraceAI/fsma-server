// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteHarvest
import com.foodtraceai.repository.cte.CteHarvestRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class CteHarvestService(
    cteHarvestRepository: CteHarvestRepository
) : BaseService<CteHarvest>(cteHarvestRepository, "CteHarvest")
