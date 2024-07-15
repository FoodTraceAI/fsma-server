// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.service

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.repository.FsmaUserRepository
import com.foodtraceai.util.EntityExistsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class FsmaUserService(
    private val fsmaUserRepository: FsmaUserRepository,
    private val passwordEncoder: PasswordEncoder,
) : BaseService<FsmaUser>(fsmaUserRepository, "FsmaUser") {

    override fun insert(entity: FsmaUser): FsmaUser {
        if (findByEmailIgnoreCase(entity.email) != null)
            throw EntityExistsException("User already exists: ${entity.email}")

        val finalEntity = entity.copy(
            password = passwordEncoder.encode(entity.password),
            email = entity.email.lowercase()
        )

        return super.insert(finalEntity)
    }

    fun findByEmailIgnoreCase(email: String): FsmaUser? =
        fsmaUserRepository.findByEmailIgnoreCase(email)
}
