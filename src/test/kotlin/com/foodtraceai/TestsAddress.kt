// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.AddressDto
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

    private lateinit var addressDto: AddressDto
    private lateinit var addressDtoUpdated: AddressDto

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
            lon = -88.162270
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
            jsonPath("$.lon") { value(addressDto.lon) }
        }.andReturn()
        // AddressId added if ne
        // val addressId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get address`() {
        val addressId = addressService.insert(addressDto.toAddress()).id
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
        val addressId = addressService.insert(addressDto.toAddress()).id
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
            jsonPath("$.lon") { value(addressDtoUpdated.lon) }
        }
    }

    @Test
    fun `delete address`() {
        val addressId = addressService.insert(addressDto.toAddress()).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.delete("/api/v1/address/$addressId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}
