// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.AddressRequestDto
import com.foodtraceai.model.toAddress
import com.foodtraceai.util.Country
import com.foodtraceai.util.UsaCanadaState
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@AutoConfigureMockMvc
class TestsAddress : TestsBase() {

    private lateinit var addressRequestDto: AddressRequestDto
    private lateinit var addressRequestDtoUpdated: AddressRequestDto

    @BeforeAll
    fun localSetup() {

        // -- Address
        addressRequestDto = AddressRequestDto(
            street = "1413 Durness Court",
            street2 = "Apt-101",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 41.742220,
            lon = -88.162270
        )
        addressRequestDtoUpdated = AddressRequestDto(
            street = "1413 Durness Court",
            street2 = "Changed",
            city = "Naperville",
            state = UsaCanadaState.IL,
            postalCode = "60565",
            country = Country.USA,
            lat = 41.742220,
            lon = -88.162270
        )
    }

    @AfterAll
    fun teardown() {
    }

    // ------------------------------------------------------------------------

    @Test
    fun `add address`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val addressId = 8   // DataLoader loads first 4 addresses, other unit tests add 3 and delete 1
        val mvcResult = mockMvc.post("/api/v1/address") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(addressRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(addressId) }
            jsonPath("$.street") { value(addressRequestDto.street) }
            jsonPath("$.street2") { value(addressRequestDto.street2) }
            jsonPath("$.city") { value(addressRequestDto.city) }
            jsonPath("$.state") { value(addressRequestDto.state.name) }
            jsonPath("$.postalCode") { value(addressRequestDto.postalCode) }
            jsonPath("$.country") { value(addressRequestDto.country.name) }
            jsonPath("$.lat") { value(addressRequestDto.lat) }
            jsonPath("$.lon") { value(addressRequestDto.lon) }
        }.andReturn()
        // AddressId added if ne
        // val addressId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get address`() {
        val addressId = addressService.insert(addressRequestDto.toAddress(0)).id
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
            jsonPath("$.lon") { value(-88.162270) }
        }
    }

    @Test
    fun `update address`() {
        val addressId = addressService.insert(addressRequestDto.toAddress()).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.put("/api/v1/address/$addressId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(addressRequestDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(addressId) }
            jsonPath("$.street") { value(addressRequestDtoUpdated.street) }
            jsonPath("$.street2") { value(addressRequestDtoUpdated.street2) }
            jsonPath("$.city") { value(addressRequestDtoUpdated.city) }
            jsonPath("$.state") { value(addressRequestDtoUpdated.state.name) }
            jsonPath("$.postalCode") { value(addressRequestDtoUpdated.postalCode) }
            jsonPath("$.country") { value(addressRequestDtoUpdated.country.name) }
            jsonPath("$.lat") { value(addressRequestDtoUpdated.lat) }
            jsonPath("$.lon") { value(addressRequestDtoUpdated.lon) }
        }
    }

    @Test
    fun `delete address`() {
        val addressId = addressService.insert(addressRequestDto.toAddress(0)).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.delete("/api/v1/address/$addressId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}
