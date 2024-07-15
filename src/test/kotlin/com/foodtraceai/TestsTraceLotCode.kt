// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.model.TraceLotCodeDto
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsTraceLotCode {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    // ------------------------------------------------------------------------
    // Test setup
    private lateinit var traceLotCodeDto: TraceLotCodeDto
    private var traceLotCodeId: Long = 0
    private val rootAuthLogin = AuthLogin(email = "root@foodtraceai.com", password = "123", refreshToken = null)
    private lateinit var accessToken: String

    fun authenticate(authLogin: AuthLogin): Pair<String, String> {
        val mvcResult = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authLogin)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()

        return Pair(
            JsonPath.read(mvcResult.response.contentAsString, "$.accessToken"),
            JsonPath.read(mvcResult.response.contentAsString, "$.refreshToken"),
        )
    }

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
            batch = "batch trace lot code 1",
            gtin = null,
            tlcDate = null,
            tlcDateType = null,
            sscc = null,
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
            jsonPath(".batch") { value("batch trace lot code 1") }
        }
    }
}
