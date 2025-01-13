// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.get


@SpringBootTest
@AutoConfigureMockMvc
class TestsSpreadsheet:TestsBase() {

    private lateinit var accessToken: String

    @BeforeAll
    fun beforeAll() {
        accessToken = authenticate(rootAuthLogin).first
    }

    // ------------------------------------------------------------------------

    @Test
    fun test_spreadsheet() {
        mockMvc.get("/api/v1/sheet//cte?which=receive") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isCreated() }
        }
//        spreadsheetService.makeSortableSpreadsheet()
    }
}

