// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

//@Entity
data class TraceabilityPlan(
    @Id @GeneratedValue
    override val id: Long = 0,
    val descRecordMaintenance: String,
    val descIdentifyFoods: String,
    val descTraceabilityLotCodes: String,
    val farmMap: String,
    val pointOfContact: String,
) : BaseModel<TraceabilityPlan>()
