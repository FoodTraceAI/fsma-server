// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class TestsCteReceive : TestsBase() {

    @Test
    fun `findAll as Root`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val mvcResult = mockMvc.get("/api/v1/cte/receive/findAll") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            // TODO: Ask Milo how to fix next line
            // jsonPath("$") { isArray() }
            // 3 Cte's are loaded by DataLoader
            jsonPath("$.length()") { value(3) }
        }.andReturn()
    }

    @Test
    fun `findAll as User1`() {
        val (accessToken, _) = authenticate(user1AuthLogin)
        val mvcResult = mockMvc.get("/api/v1/cte/receive/findAll") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            // No
            jsonPath("$.length()") { value(0) }
        }.andReturn()
    }
}