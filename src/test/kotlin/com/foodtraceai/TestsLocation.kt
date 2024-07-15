// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.model.Location
import com.foodtraceai.model.LocationDto
import com.foodtraceai.model.toLocation
import com.foodtraceai.service.AddressService
import com.foodtraceai.service.FoodBusService
import com.foodtraceai.service.LocationService
import com.foodtraceai.util.EntityNotFoundException
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
class TestsLocation {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // ------------------------------------------------------------------------
    // Test setup

    @Autowired
    private lateinit var addressService: AddressService

    @Autowired
    private lateinit var foodBusService: FoodBusService

    @Autowired
    private lateinit var locationService: LocationService

    private lateinit var locationDto: LocationDto
    private lateinit var locationDtoUpdated: LocationDto

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

    @BeforeEach
    fun localSetup() {
        locationDto = LocationDto(
            id = 0,
            foodBusId = 1,
            contactName = "Steve",
            contactPhone = "1-800-555-1212",
            contactEmail = "steve@gmail.abc",
            addressId = 1,
            isBillable = true,
        )

        locationDtoUpdated = LocationDto(
            id = 0,
            foodBusId = 1,
            contactName = "NewContact",
            contactPhone = "0-000-000-0000",
            contactEmail = "newcontact@gmail.abc",
            addressId = 1,
            isBillable = false,
        )
    }


    // ------------------------------------------------------------------------

    private fun addLocation(locationDto: LocationDto): Location {
        val foodBus = foodBusService.findById(locationDto.foodBusId)
            ?: throw EntityNotFoundException("Location not found = ${locationDto.foodBusId}")

        val address = addressService.findById(locationDto.addressId)
            ?: throw EntityNotFoundException("Address not found = ${locationDto.addressId}")

        return locationService.insert(locationDto.toLocation(foodBus, address))
    }

    @Test
    fun `add location`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val mvcResult = mockMvc.post("/api/v1/location") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(locationDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.foodBusId") { value(locationDto.foodBusId) }
            jsonPath("$.contactName") { value(locationDto.contactName) }
            jsonPath("$.contactPhone") { value(locationDto.contactPhone) }
            jsonPath("$.contactEmail") { value(locationDto.contactEmail) }
            jsonPath("$.addressId") { value(locationDto.addressId) }
            jsonPath("$.isBillable") { value(true) }
        }.andReturn()
        val locationId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get location`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val locationId = addLocation(locationDto).id
        mockMvc.get("/api/v1/location/$locationId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.foodBusId") { value(locationDto.foodBusId) }
            jsonPath("$.contactName") { value(locationDto.contactName) }
            jsonPath("$.contactPhone") { value(locationDto.contactPhone) }
            jsonPath("$.contactEmail") { value(locationDto.contactEmail) }
            jsonPath("$.addressId") { value(locationDto.addressId) }
            jsonPath("$.isBillable") { value(true) }
        }
    }

    @Test
    fun `update location`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val locationId: Long = addLocation(locationDto).id
        locationDtoUpdated = locationDtoUpdated.copy(id = locationId)
        mockMvc.put("/api/v1/location/$locationId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(locationDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(locationId) }
            jsonPath("$.foodBusId") { value(locationDtoUpdated.foodBusId) }
            jsonPath("$.contactName") { value(locationDtoUpdated.contactName) }
            jsonPath("$.contactPhone") { value(locationDtoUpdated.contactPhone) }
            jsonPath("$.contactEmail") { value(locationDtoUpdated.contactEmail) }
            jsonPath("$.addressId") { value(locationDtoUpdated.addressId) }
            jsonPath("$.isBillable") { value(false) }
        }
    }

    @Test
    fun `delete business`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val locationId: Long = addLocation(locationDto).id
        mockMvc.delete("/api/v1/location/$locationId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}