// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.Contact
import com.foodtraceai.model.TracePlanDto
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
class TestsTracePlan:TestsBase() {

    private lateinit var tracePlanContact: Contact
    private lateinit var updatedTracePlanContact: Contact

    @Autowired
    private lateinit var tracePlanService: TracePlanService

    private lateinit var tracePlanDto: TracePlanDto
    private lateinit var tracePlanDtoUpdated: TracePlanDto

    @BeforeEach
    fun localSetup() {
        tracePlanContact = contactService.insert(
            Contact(
                firstName = "tracePlanContact firstName",
                lastName = "tracePlanContact lastName",
                email = "tracePlanContact email",
                phone = "tracePlanContact 800-555-1212"
            )
        )
        updatedTracePlanContact = contactService.insert(
            Contact(
                firstName = "Updated - tracePlanContact firstName",
                lastName = "Updated - tracePlanContact lastName",
                email = "Updated - tracePlanContact email",
                phone = "Updated - tracePlanContact 800-555-1212"
            )
        )

        tracePlanDto = TracePlanDto(
            locationId = 1,
            descProcRecordMaintenance = "descProcRecordMaintenance",
            descProcIdentifyFoods = "descProcIdentifyFoods",
            descAssignTraceLotCodes = "descAssignTraceLotCodes",
            tracePlanContactId = tracePlanContact.id
        )

        tracePlanDtoUpdated = TracePlanDto(
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
            content = objectMapper.writeValueAsString(tracePlanDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.descProcRecordMaintenance") { value(tracePlanDto.descProcRecordMaintenance) }
            jsonPath("$.descProcIdentifyFoods") { value(tracePlanDto.descProcIdentifyFoods) }
            jsonPath("$.descAssignTraceLotCodes") { value(tracePlanDto.descAssignTraceLotCodes) }
            jsonPath("$.tracePlanContactId") { value(tracePlanContact.id) }
        }.andReturn()
        val tracePlanId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get TracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val location = locationService.findById(tracePlanDto.locationId)
            ?: throw EntityNotFoundException("LocationId not found = ${tracePlanDto.locationId}")

        val tracePlanId = tracePlanService.insert(tracePlanDto.toTracePlan(location,tracePlanContact)).id
        mockMvc.get("/api/v1/trace-plan/$tracePlanId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(2) }
            jsonPath("$.descProcRecordMaintenance") { value(tracePlanDto.descProcRecordMaintenance) }
            jsonPath("$.descProcIdentifyFoods") { value(tracePlanDto.descProcIdentifyFoods) }
            jsonPath("$.descAssignTraceLotCodes") { value(tracePlanDto.descAssignTraceLotCodes) }
            jsonPath("$.tracePlanContactId") { value(tracePlanContact.id) }
        }
    }

    @Test
    fun `delete tracePlan`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val location = locationService.findById(tracePlanDto.locationId)
            ?: throw EntityNotFoundException("LocationId not found = ${tracePlanDto.locationId}")
        val tracePlanId = tracePlanService.insert(tracePlanDto.toTracePlan(location,tracePlanContact)).id
        mockMvc.delete("/api/v1/traceplan/$tracePlanId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}