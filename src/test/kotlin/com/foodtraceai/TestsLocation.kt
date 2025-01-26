// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.Location
import com.foodtraceai.model.LocationRequestDto
import com.foodtraceai.model.toLocation
import com.foodtraceai.util.EntityNotFoundException
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestsLocation : TestsBase() {

    private lateinit var locationRequestDto: LocationRequestDto
    private lateinit var locationRequestDtoUpdated: LocationRequestDto

    @BeforeEach
    fun localSetup() {
        locationRequestDto = LocationRequestDto(
            foodBusId = 1,
            locationContactId = 5, // Steve Eick Contact
            description = "Steve's House",
            addressId = 1,
            isClient = true,
        )

        locationRequestDtoUpdated = LocationRequestDto(
            foodBusId = 1,
            locationContactId = 3,  // "NewContactFirst
            description = "NewContact",
            addressId = 1,
            isClient = false,
        )
    }

    // ------------------------------------------------------------------------

    private fun addLocation(locationRequestDto: LocationRequestDto): Location {
        val foodBus = foodBusService.findById(locationRequestDto.foodBusId)
            ?: throw EntityNotFoundException("FoodBusId not found = ${locationRequestDto.foodBusId}")

        val address = addressService.findById(locationRequestDto.addressId)
            ?: throw EntityNotFoundException("Address not found = ${locationRequestDto.addressId}")

        val locationContact = contactService.findById(locationRequestDto.locationContactId)
            ?: throw EntityNotFoundException("Contact not found = ${locationRequestDto.locationContactId}")

        return locationService.insert(
            locationRequestDto.toLocation(
                id = 0,
                foodBus,
                locationContact = locationContact,
                address = address
            )
        )
    }

    @Test
    @Order(1)
    fun `add location`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val mvcResult = mockMvc.post("/api/v1/location") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(locationRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.foodBusId") { value(locationRequestDto.foodBusId) }
            jsonPath("$.locationContactId") { value(locationRequestDto.locationContactId) }
            jsonPath("$.addressId") { value(locationRequestDto.addressId) }
        }.andReturn()
        val locationId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    @Order(2)
    fun `get location`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val locationId = addLocation(locationRequestDto).id
        mockMvc.get("/api/v1/location/$locationId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.foodBusId") { value(locationRequestDto.foodBusId) }
            jsonPath("$.locationContactId") { value(locationRequestDto.locationContactId) }
            jsonPath("$.addressId") { value(locationRequestDto.addressId) }
            jsonPath("$.isClient") { value(true) }
        }
    }

    @Test
    @Order(3)
    fun `update location`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val locationId: Long = addLocation(locationRequestDto).id
        mockMvc.put("/api/v1/location/$locationId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(locationRequestDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(locationId) }
            jsonPath("$.foodBusId") { value(locationRequestDtoUpdated.foodBusId) }
            jsonPath("$.locationContactId") { value(locationRequestDtoUpdated.locationContactId) }
            jsonPath("$.addressId") { value(locationRequestDtoUpdated.addressId) }
            jsonPath("$.isClient") { value(false) }
        }
    }

    @Test
    @Order(4)
    fun `delete business`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val locationId: Long = addLocation(locationRequestDto).id
        mockMvc.delete("/api/v1/location/$locationId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}