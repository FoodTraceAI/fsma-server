// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.controller.TraceLotCodeController
import com.foodtraceai.model.TraceLotCodeDto
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsTraceLotCode : TestsBase() {

    // ------------------------------------------------------------------------
    // Test setup
    private lateinit var traceLotCodeDto: TraceLotCodeDto
    private var traceLotCodeId: Long = 0
    private lateinit var accessToken: String

    fun addTraceLotCode(): Long {
        val mvcResult = mockMvc.post("/api/v1/tlc") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(traceLotCodeDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()
        return JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @BeforeEach
    fun setup() {
        accessToken = authenticate(rootAuthLogin).first

        traceLotCodeDto = TraceLotCodeDto(
            tlcVal = "trace lot code 1",
            batchLot = "batchLot_1001",
            gtin = null,
            packDate = null,
            harvestDate = null,
            sscc = null,
            tlcSourceId = 1
        )

        traceLotCodeId = addTraceLotCode()
    }

    // ------------------------------------------------------------------------

    @Test
    fun `get trace lot code`() {
        mockMvc.get("/api/v1/tlc/$traceLotCodeId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(traceLotCodeId) }
            jsonPath("$.tlcVal") { value("trace lot code 1") }
            jsonPath("$.batchLot") { value("batchLot_1001") }
        }
    }

    @Test
    fun `make trace lot code`() {
        val traceLotCodeArgs = TraceLotCodeController.TraceLotCodeArgs(
            tlcVal = "tlcArgsVal",
            batchLot = "batchLot_Args_sourceId2",
            tlcSourceId = 2
        )

        mockMvc.post("/api/v1/tlc/makeTraceLotCode") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(traceLotCodeArgs)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.tlcVal") { value(traceLotCodeArgs.tlcVal) }
            jsonPath("$.batchLot") { value(traceLotCodeArgs.batchLot) }
            jsonPath("$.tlcSourceId") { value(traceLotCodeArgs.tlcSourceId) }
        }
    }
}
