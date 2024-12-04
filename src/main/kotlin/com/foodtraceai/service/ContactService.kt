// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.Contact
import com.foodtraceai.repository.ContactRepository
import org.springframework.stereotype.Service

@Service
class ContactService(
    contactRepository: ContactRepository
) : BaseService<Contact>(contactRepository, "Contact")
