// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.Reseller
import com.foodtraceai.repository.ResellerRepository
import org.springframework.stereotype.Service

@Service
class ResellerService(
    resellerRepository: ResellerRepository
) : BaseService<Reseller>(resellerRepository, "Reseller")
