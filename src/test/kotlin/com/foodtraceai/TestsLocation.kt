// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.Location
import com.foodtraceai.model.LocationDto
import com.foodtraceai.model.toLocation
import com.foodtraceai.util.EntityNotFoundException
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
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
class TestsLocation:TestsBase() {

    private lateinit var locationDto: LocationDto
    private lateinit var locationDtoUpdated: LocationDto

    @BeforeEach
    fun localSetup() {
        locationDto = LocationDto(
            id = 0,
            foodBusId = 1,
            locationContactId = 5, // Steve Eick Contact
            description = "Steve's House",
            addressId = 1,
            isClient = true,
        )

        locationDtoUpdated = LocationDto(
            id = 0,
            foodBusId = 1,
            locationContactId = 3,  // "NewContactFirst
            description = "NewContact",
            addressId = 1,
            isClient = false,
        )
    }

    // ------------------------------------------------------------------------

    private fun addLocation(locationDto: LocationDto): Location {
        val foodBus = foodBusService.findById(locationDto.foodBusId)
            ?: throw EntityNotFoundException("FoodBusId not found = ${locationDto.foodBusId}")

        val address = addressService.findById(locationDto.addressId)
            ?: throw EntityNotFoundException("Address not found = ${locationDto.addressId}")

        val locationContact = contactService.findById(locationDto.locationContactId)
            ?: throw EntityNotFoundException("Contact not found = ${locationDto.locationContactId}")

        return locationService.insert(
            locationDto.toLocation(
                foodBus,
                locationContact = locationContact,
                address = address
            )
        )
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
            jsonPath("$.locationContactId") { value(locationDto.locationContactId) }
            jsonPath("$.addressId") { value(locationDto.addressId) }
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
            jsonPath("$.locationContactId") { value(locationDto.locationContactId) }
            jsonPath("$.addressId") { value(locationDto.addressId) }
            jsonPath("$.isClient") { value(true) }
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
            jsonPath("$.locationContactId") { value(locationDtoUpdated.locationContactId) }
            jsonPath("$.addressId") { value(locationDtoUpdated.addressId) }
            jsonPath("$.isClient") { value(false) }
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