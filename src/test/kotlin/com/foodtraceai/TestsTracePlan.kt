// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.model.TracePlanDto
import com.foodtraceai.model.toTracePlan
import com.foodtraceai.service.LocationService
import com.foodtraceai.service.TracePlanService
import com.foodtraceai.util.EntityNotFoundException
import com.foodtraceai.util.PointOfContact
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsTracePlan {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // ------------------------------------------------------------------------
    // Test setup

    @Autowired
    private lateinit var locationService: LocationService

    @Autowired
    private lateinit var tracePlanService: TracePlanService

    private lateinit var tracePlanDto: TracePlanDto
    private lateinit var tracePlanDtoUpdated: TracePlanDto

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
        tracePlanDto = TracePlanDto(
            locationId = 1,
            descProcRecordMaintenance = "descProcRecordMaintenance",
            descProcIdentifyFoods = "descProcIdentifyFoods",
            descAssignTraceLotCodes = "descAssignTraceLotCodes",
            pointOfContact = PointOfContact("POC Name", "Poc email", phone = "800-555-1212")
        )

        tracePlanDtoUpdated = TracePlanDto(
            locationId = 1,
            descProcRecordMaintenance = "Updated - descProcRecordMaintenance",
            descProcIdentifyFoods = "Updated - descProcIdentifyFoods",
            descAssignTraceLotCodes = "Updated - descAssignTraceLotCodes",
            pointOfContact = PointOfContact(
                name = "Updated - POC Name",
                email = "Updated - Poc email",
                phone = "Updated - 800-555-1212"
            )
        )
    }


    // ------------------------------------------------------------------------

    @Test
    fun `add TracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val mvcResult = mockMvc.post("/api/v1/trace-plan") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(tracePlanDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.descProcRecordMaintenance") { value(tracePlanDto.descProcRecordMaintenance) }
            jsonPath("$.descProcIdentifyFoods") { value(tracePlanDto.descProcIdentifyFoods) }
            jsonPath("$.descAssignTraceLotCodes") { value(tracePlanDto.descAssignTraceLotCodes) }
            jsonPath("$.pointOfContact.name") { value(tracePlanDto.pointOfContact.name) }
            jsonPath("$.pointOfContact.email") { value(tracePlanDto.pointOfContact.email) }
            jsonPath("$.pointOfContact.phone") { value(tracePlanDto.pointOfContact.phone) }
        }.andReturn()
        val tracePlanId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get TracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val location = locationService.findById(tracePlanDto.locationId)
            ?: throw EntityNotFoundException("LocationId not found = ${tracePlanDto.locationId}")

        val tracePlanId = tracePlanService.insert(tracePlanDto.toTracePlan(location)).id
        mockMvc.get("/api/v1/trace-plan/$tracePlanId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(2) }
            jsonPath("$.descProcRecordMaintenance") { value(tracePlanDto.descProcRecordMaintenance) }
            jsonPath("$.descProcIdentifyFoods") { value(tracePlanDto.descProcIdentifyFoods) }
            jsonPath("$.descAssignTraceLotCodes") { value(tracePlanDto.descAssignTraceLotCodes) }
            jsonPath("$.pointOfContact.name") { value(tracePlanDto.pointOfContact.name) }
            jsonPath("$.pointOfContact.email") { value(tracePlanDto.pointOfContact.email) }
            jsonPath("$.pointOfContact.phone") { value(tracePlanDto.pointOfContact.phone) }
        }
    }

    @Test
    fun `delete business`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val location = locationService.findById(tracePlanDto.locationId)
            ?: throw EntityNotFoundException("LocationId not found = ${tracePlanDto.locationId}")
        val tracePlanId = tracePlanService.insert(tracePlanDto.toTracePlan(location)).id
        mockMvc.delete("/api/v1/traceplan/$tracePlanId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}