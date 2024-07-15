// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.cte

import com.foodtraceai.model.cte.CteIPackProd
import com.foodtraceai.repository.cte.CteIPackProdRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class CteIPackProdService(
    cteIPackProdRepository: CteIPackProdRepository
) : BaseService<CteIPackProd>(cteIPackProdRepository, "CteIPack")
