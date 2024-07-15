// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteIPackExempt
import com.foodtraceai.repository.cte.CteIPackExemptRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class CteIPackExemptService(
    cteIPackExempt: CteIPackExemptRepository
) : BaseService<CteIPackExempt>(cteIPackExempt, "CteIPackExempt")
