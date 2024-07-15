// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.Address
import com.foodtraceai.repository.AddressRepository
import org.springframework.stereotype.Service

@Service
class AddressService(
     addressRepository: AddressRepository
) : BaseService<Address>(addressRepository, "Address")
