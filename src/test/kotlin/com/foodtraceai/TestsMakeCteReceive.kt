// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.controller.cte.CteReceiveController
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import java.time.OffsetDateTime

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestsMakeCteReceive : TestsBase() {

    private val createArgs = CteReceiveController.SupShipArgs(
        sscc = "sscc2",
        tlcId = 2,
        receiveLocationId = 2,
        receiveDate = LocalDate.of(2024, 10, 18),
        receiveTime = OffsetDateTime.now(),
    )

    private val notFoundArgs = CteReceiveController.SupShipArgs(
        sscc = "****Not Found****",
        tlcId = 2,
        receiveLocationId = 3,
        receiveDate = LocalDate.of(2024, 10, 18),
        receiveTime = OffsetDateTime.now(),
    )

    private lateinit var accessToken: String

    @BeforeAll
    fun beforeAll() {
        val (access, _) = authenticate(user0AuthLogin)
        accessToken = access
    }

    @BeforeEach
    fun localSetup() {
    }

    // ------------------------------------------------------------------------

    @Test
    @Order(1)
    fun makeCteCreated() {
        mockMvc.post("/api/v1/cte/receive/makeCteReceive") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(createArgs)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$") { value("Created") }
        }.andReturn()
    }

    @Test
    @Order(2)
    fun makeCteAlreadyExists() {
        mockMvc.post("/api/v1/cte/receive/makeCteReceive") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(createArgs)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$") { value("AlreadyExists") }
        }.andReturn()
    }

    @Test
    @Order(3)
    fun makeCteSupShipCteNotFound() {
        mockMvc.post("/api/v1/cte/receive/makeCteReceive") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(notFoundArgs)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$") { value("SupShipCteNotFound") }
        }.andReturn()
    }

}