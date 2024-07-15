// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.auth

data class ResetPasswordRequestDto(
    val email: String
)

data class RequestEmailPasswordResetDto(
    val email: String,
    val passwordResetToken: String
)

data class ChangePasswordDto(
    val passwordResetToken: String,
    val newPassword: String
)
