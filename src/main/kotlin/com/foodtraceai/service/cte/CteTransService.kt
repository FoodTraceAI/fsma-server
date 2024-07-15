// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteTrans
import com.foodtraceai.repository.cte.CteTransRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class CteTransService(
    cteTransRepository: CteTransRepository
) : BaseService<CteTrans>(cteTransRepository, "CteTrans")
