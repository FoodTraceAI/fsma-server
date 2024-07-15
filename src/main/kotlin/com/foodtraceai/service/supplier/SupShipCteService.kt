// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service.supplier

import com.foodtraceai.model.supplier.SupShipCte
import com.foodtraceai.repository.supplier.SupShipCteRepository
import com.foodtraceai.service.BaseService
import org.springframework.stereotype.Service

@Service
class SupShipCteService(
    supShipCteRepository: SupShipCteRepository
) : BaseService<SupShipCte>(supShipCteRepository, "SupShipCte")
