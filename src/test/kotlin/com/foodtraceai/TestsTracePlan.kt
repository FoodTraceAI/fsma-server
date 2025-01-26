// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.Contact
import com.foodtraceai.model.TracePlanRequestDto
import com.foodtraceai.model.toTracePlan
import com.foodtraceai.service.TracePlanService
import com.foodtraceai.util.EntityNotFoundException
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsTracePlan : TestsBase() {

    private lateinit var tracePlanContact: Contact
    private lateinit var updatedTracePlanContact: Contact

    @Autowired
    private lateinit var tracePlanService: TracePlanService

    private lateinit var tracePlanRequestDto: TracePlanRequestDto
    private lateinit var tracePlanRequestDtoUpdated: TracePlanRequestDto

    @BeforeEach
    fun localSetup() {
        tracePlanContact = contactService.insert(
            Contact(
                firstname = "tracePlanContact firstname",
                lastname = "tracePlanContact lastname",
                email = "tracePlanContact email",
                phone = "tracePlanContact 800-555-1212"
            )
        )
        updatedTracePlanContact = contactService.insert(
            Contact(
                firstname = "Updated - tracePlanContact firstname",
                lastname = "Updated - tracePlanContact lastname",
                email = "Updated - tracePlanContact email",
                phone = "Updated - tracePlanContact 800-555-1212"
            )
        )

        tracePlanRequestDto = TracePlanRequestDto(
            locationId = 1,
            descProcRecordMaintenance = "descProcRecordMaintenance",
            descProcIdentifyFoods = "descProcIdentifyFoods",
            descAssignTraceLotCodes = "descAssignTraceLotCodes",
            tracePlanContactId = tracePlanContact.id
        )

        tracePlanRequestDtoUpdated = TracePlanRequestDto(
            locationId = 1,
            descProcRecordMaintenance = "Updated - descProcRecordMaintenance",
            descProcIdentifyFoods = "Updated - descProcIdentifyFoods",
            descAssignTraceLotCodes = "Updated - descAssignTraceLotCodes",
            tracePlanContactId = updatedTracePlanContact.id,
        )
    }

    // ------------------------------------------------------------------------

    @Test
    fun `add TracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val mvcResult = mockMvc.post("/api/v1/trace-plan") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(tracePlanRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.descProcRecordMaintenance") { value(tracePlanRequestDto.descProcRecordMaintenance) }
            jsonPath("$.descProcIdentifyFoods") { value(tracePlanRequestDto.descProcIdentifyFoods) }
            jsonPath("$.descAssignTraceLotCodes") { value(tracePlanRequestDto.descAssignTraceLotCodes) }
            jsonPath("$.tracePlanContactId") { value(tracePlanContact.id) }
        }.andReturn()
        val tracePlanId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get TracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val location = locationService.findById(tracePlanRequestDto.locationId)
            ?: throw EntityNotFoundException("LocationId not found = ${tracePlanRequestDto.locationId}")

        val tracePlanId =
            tracePlanService.insert(tracePlanRequestDto.toTracePlan(id = 0, location, tracePlanContact)).id
        mockMvc.get("/api/v1/trace-plan/$tracePlanId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(2) }
            jsonPath("$.descProcRecordMaintenance") { value(tracePlanRequestDto.descProcRecordMaintenance) }
            jsonPath("$.descProcIdentifyFoods") { value(tracePlanRequestDto.descProcIdentifyFoods) }
            jsonPath("$.descAssignTraceLotCodes") { value(tracePlanRequestDto.descAssignTraceLotCodes) }
            jsonPath("$.tracePlanContactId") { value(tracePlanContact.id) }
        }
    }

    @Test
    fun `delete tracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val location = locationService.findById(tracePlanRequestDto.locationId)
            ?: throw EntityNotFoundException("LocationId not found = ${tracePlanRequestDto.locationId}")
        val tracePlanId =
            tracePlanService.insert(tracePlanRequestDto.toTracePlan(id = 0, location, tracePlanContact)).id
        mockMvc.delete("/api/v1/traceplan/$tracePlanId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}