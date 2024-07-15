// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.model.AddressDto
import com.foodtraceai.model.toAddress
import com.foodtraceai.service.AddressService
import com.foodtraceai.util.Country
import com.foodtraceai.util.UsaCanadaState
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
class TestsAddress {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // ------------------------------------------------------------------------
    // Test setup

    @Autowired
    private lateinit var addressService: AddressService

    private lateinit var addressDto: AddressDto
    private lateinit var addressDtoUpdated: AddressDto
    private val resellerId = 1L
    private val rootAuthLogin = AuthLogin(email = "root@foodtraceai.com", password = "123", refreshToken = null)

    private fun authenticate(authLogin: AuthLogin): Pair<String, String> {
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

    @BeforeAll
    fun localSetup() {

        // -- Address
        addressDto = AddressDto(
            id = 0,
            resellerId = 1,
            street = "1413 Durness Court",
            street2 = "Apt-101",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 41.742220,
            lng = -88.162270
        )
        addressDtoUpdated = AddressDto(
            id = 0,
            resellerId = 1,
            street = "1413 Durness Court",
            street2 = "Changed",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 41.742220,
            lng = -88.162270
        )
    }

    @AfterAll
    fun teardown() {
    }

    // ------------------------------------------------------------------------

    @Test
    fun `add address`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val addressId = 7   // DataLoader loads first 3 addresses, other unit tests add 3 and delete 1
        val mvcResult = mockMvc.post("/api/v1/address") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(addressDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(addressId) }
            jsonPath("$.street") { value(addressDto.street) }
            jsonPath("$.street2") { value(addressDto.street2) }
            jsonPath("$.city") { value(addressDto.city) }
            jsonPath("$.state") { value(addressDto.state.name) }
            jsonPath("$.postalCode") { value(addressDto.postalCode) }
            jsonPath("$.country") { value(addressDto.country.name) }
            jsonPath("$.lat") { value(addressDto.lat) }
            jsonPath("$.lng") { value(addressDto.lng) }
        }.andReturn()

        // AddressId added if ne
        // val addressId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get address`() {
        val addressId = addressService.insert(addressDto.toAddress(resellerId)).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.get("/api/v1/address/$addressId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(addressId) }
            jsonPath("$.street") { value("1413 Durness Court") }
            jsonPath("$.street2") { value("Apt-101") }
            jsonPath("$.city") { value("Naperville") }
            jsonPath("$.state") { value("IL") }
            jsonPath("$.postalCode") { value("60565") }
            jsonPath("$.country") { value("USA") }
            jsonPath("$.lat") { value(41.742220) }
            jsonPath("$.lng") { value(-88.162270) }
        }
    }

    @Test
    fun `update address`() {
        val addressId = addressService.insert(addressDto.toAddress(resellerId)).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        addressDtoUpdated = addressDtoUpdated.copy(id = addressId)
        mockMvc.put("/api/v1/address/$addressId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(addressDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(addressId) }
            jsonPath("$.street") { value(addressDtoUpdated.street) }
            jsonPath("$.street2") { value(addressDtoUpdated.street2) }
            jsonPath("$.city") { value(addressDtoUpdated.city) }
            jsonPath("$.state") { value(addressDtoUpdated.state.name) }
            jsonPath("$.postalCode") { value(addressDtoUpdated.postalCode) }
            jsonPath("$.country") { value(addressDtoUpdated.country.name) }
            jsonPath("$.lat") { value(addressDtoUpdated.lat) }
            jsonPath("$.lng") { value(addressDtoUpdated.lng) }
        }
    }

    @Test
    fun `delete address`() {
        val addressId = addressService.insert(addressDto.toAddress(resellerId)).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.delete("/api/v1/address/$addressId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}