// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class TestsSupShipCte : TestsBase() {
    private lateinit var accessToken: String

    @BeforeAll
    fun beforeAll() {
        accessToken = authenticate(rootAuthLogin).first
    }

    @AfterAll
    fun afterAll() {
    }

    @BeforeEach
    fun beforeEach() {
    }

    // ------------------------------------------------------------------------

    private fun findById(id: Int) {
        mockMvc.get("/api/v1/supplier/shipCte/$id") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(id) }
        }
    }

    @Test
    fun findByIdX() {
        findById(1)
        findById(2)
        findById(3)
    }

    @Test
    fun findAll() {
        mockMvc.get("/api/v1/supplier/shipCte/findAll") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(3) }
        }
    }
}