// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Duration
import java.time.temporal.ChronoUnit

// JsonString --> List
fun String.toList(): List<String> {
    val typeRef = object : TypeReference<List<String>>() {}
    return jacksonObjectMapper().readValue(this, typeRef)
}

// List --> JsonString
fun List<String>.toJsonString(): String =
    jacksonObjectMapper().writeValueAsString(this)

val Int.hours: Duration
    get() = Duration.of(this * 60L * 60L * 1000L, ChronoUnit.MILLIS)

val Int.days: Duration
    get() = Duration.of(this * 24L * 60L * 60L * 1000L, ChronoUnit.MILLIS)