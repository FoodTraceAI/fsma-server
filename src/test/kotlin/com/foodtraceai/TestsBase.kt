// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.service.*
import com.jayway.jsonpath.JsonPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsBase {

    @Autowired protected lateinit var mockMvc: MockMvc
    @Autowired protected lateinit var objectMapper: ObjectMapper

    @Autowired protected lateinit var addressService: AddressService
    @Autowired protected lateinit var contactService: ContactService
    @Autowired protected lateinit var foodBusService: FoodBusService
    @Autowired protected lateinit var franchisorService: FranchisorService
    @Autowired protected lateinit var locationService: LocationService
    @Autowired protected lateinit var resellerService: ResellerService

    protected val rootAuthLogin = AuthLogin(email = "root@foodtraceai.com", password = "123", refreshToken = null)
    protected val user0AuthLogin = AuthLogin(email = "user0@foodtraceai.com", password = "123", refreshToken = null)
    protected val user1AuthLogin = AuthLogin(email = "user1@foodtraceai.com", password = "123", refreshToken = null)

    protected fun authenticate(authLogin: AuthLogin): Pair<String, String> {
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
}