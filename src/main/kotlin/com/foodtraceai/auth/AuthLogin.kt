// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.auth

data class AuthLogin(
    val email: String?,
    val password: String?,
    val refreshToken: String?,
)
