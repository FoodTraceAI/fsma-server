// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.TraceLotCodeRequestDto
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestsTraceLotCode : TestsBase() {

    // ------------------------------------------------------------------------
    // Test setup
    private lateinit var traceLotCodeRequestDto: TraceLotCodeRequestDto
    private var traceLotCodeId: Long = 0
    private lateinit var accessToken: String

    fun addTraceLotCode(): Long {
        val mvcResult = mockMvc.post("/api/v1/tlc") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(traceLotCodeRequestDto)
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

        traceLotCodeRequestDto = TraceLotCodeRequestDto(
            tlcVal = "trace lot code 1",
            batchLot = "batchLot_1001",
            gtin = null,
            packDate = null,
            harvestDate = null,
            sscc = null,
            tlcSourceLocId = 1
        )

        traceLotCodeId = addTraceLotCode()
    }

    // ------------------------------------------------------------------------

    @Test
    @Order(1)
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
    @Order(2)
    fun `make trace lot code`() {
        val traceLotCodeRequestDto = TraceLotCodeRequestDto(
            tlcVal = "tlcArgsVal",
            gtin = "gtinForTlcArgsVal",
            batchLot = "batchLot_Args_sourceId2",
            tlcSourceLocId = 2
        )

        mockMvc.post("/api/v1/tlc") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(traceLotCodeRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.tlcVal") { value(traceLotCodeRequestDto.tlcVal) }
            jsonPath("$.gtin") { value(traceLotCodeRequestDto.gtin) }
            jsonPath("$.batchLot") { value(traceLotCodeRequestDto.batchLot) }
            jsonPath("$.tlcSourceLocId") { value(traceLotCodeRequestDto.tlcSourceLocId) }
        }
    }
}
