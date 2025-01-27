// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.cte.CteReceiveRequestDto
import com.foodtraceai.model.cte.toCteReceive
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate
import java.time.OffsetDateTime

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestsCteReceive : TestsBase() {

    private lateinit var cteReceiveRequestDto: CteReceiveRequestDto
    private lateinit var cteReceiveRequestUpdatedDto: CteReceiveRequestDto
    private lateinit var accessToken: String

    @BeforeAll
    fun localSetup() {
        accessToken = authenticate(rootAuthLogin).first

        // -- Contact
        cteReceiveRequestDto = CteReceiveRequestDto(
            locationId = 3,
            ftlItem = FtlItem.Fruits,
            variety = "variety of fruits",
            tlcId = 1,
            quantity = 15,
            unitOfMeasure = UnitOfMeasure.Case,
            prodDesc = "variety of fruits",
            ipsLocationId = 2,
            receiveDate = LocalDate.now(),
            receiveTime = OffsetDateTime.now(),
            tlcSourceId = 1,
            tlcSourceReference = null,
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "BOL1",
        )
        cteReceiveRequestUpdatedDto = CteReceiveRequestDto(
            locationId = 3,
            ftlItem = FtlItem.Fruits,
            variety = "Changed Variety of fruits",
            tlcId = 1,
            quantity = 10,
            unitOfMeasure = UnitOfMeasure.Kilo,
            prodDesc = "Changed variety of fruits",
            ipsLocationId = 2,
            receiveDate = LocalDate.now(),
            receiveTime = OffsetDateTime.now(),
            tlcSourceId = 1,
            tlcSourceReference = null,
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "BOL2",
        )
    }

    @AfterAll
    fun teardown() {
    }

    private fun addCte(requestDto: CteReceiveRequestDto): CteReceive {
        val location = locationService.findById(requestDto.locationId)
            ?: throw EntityNotFoundException("CteReceive locationId: ${requestDto.locationId} not found")
        val tlc = traceLotCodeService.findById(requestDto.tlcId)
            ?: throw EntityNotFoundException("CteReceive tlcId: ${requestDto.tlcId} not found")
        val ipsLocation = locationService.findById(requestDto.ipsLocationId)
            ?: throw EntityNotFoundException("CteReceive ipsLocationId: ${requestDto.ipsLocationId} not found")
        val tlcSource = locationService.findById(requestDto.tlcSourceId)
            ?: throw EntityNotFoundException("CteReceive tlcSourceId: ${requestDto.tlcSourceId} not found")
        return cteReceiveService.insert(
            requestDto.toCteReceive(
                id = 0, location, tlc, ipsLocation, tlcSource
            )
        )
    }

    // ------------------------------------------------------------------------

    @Test
    @Order(1)
    fun `findAll CteReceive as Root`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val mvcResult = mockMvc.get("/api/v1/cte/receive/findAll") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            // TODO: Ask Milo how to fix next line
            // jsonPath("$") { isArray() }
            // 3 Cte's are loaded by DataLoader
            jsonPath("$.length()") { value(3) }
        }.andReturn()
    }

    @Test
    @Order(2)
    fun `findAll CteReceive as User1`() {
        val (accessToken, _) = authenticate(user1AuthLogin)
        val mvcResult = mockMvc.get("/api/v1/cte/receive/findAll") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            // No
            jsonPath("$.length()") { value(0) }
        }.andReturn()
    }

    @Test
    @Order(3)
    fun `add cteReceive`() {
        val cteReceiveId = 4   // DataLoader loads 3 contacts
        val mvcResult = mockMvc.post("/api/v1/cte/receive") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(cteReceiveRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(cteReceiveId) }
            jsonPath("$.ftlItem") { value(cteReceiveRequestDto.ftlItem.name) }
            jsonPath("$.variety") { value(cteReceiveRequestDto.variety) }
            jsonPath("$.tlcId") { value(cteReceiveRequestDto.tlcId) }
            jsonPath("$.quantity") { value(cteReceiveRequestDto.quantity) }
        }.andReturn()
        // val contactId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    @Order(4)
    fun `get cteReceive`() {
        val cteReceiveId = addCte(cteReceiveRequestDto).id
        mockMvc.get("/api/v1/cte/receive/$cteReceiveId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(cteReceiveId) }
            jsonPath("$.ftlItem") { value(cteReceiveRequestDto.ftlItem.name) }
            jsonPath("$.variety") { value(cteReceiveRequestDto.variety) }
            jsonPath("$.tlcId") { value(cteReceiveRequestDto.tlcId) }
            jsonPath("$.quantity") { value(cteReceiveRequestDto.quantity) }
        }
    }

    @Test
    @Order(5)
    fun `update cteReceive`() {
        val cteReceiveId = addCte(cteReceiveRequestUpdatedDto).id
        mockMvc.put("/api/v1/cte/receive/$cteReceiveId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(cteReceiveRequestUpdatedDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(cteReceiveId) }
            jsonPath("$.ftlItem") { value(cteReceiveRequestUpdatedDto.ftlItem.name) }
            jsonPath("$.variety") { value(cteReceiveRequestUpdatedDto.variety) }
            jsonPath("$.tlcId") { value(cteReceiveRequestUpdatedDto.tlcId) }
            jsonPath("$.quantity") { value(cteReceiveRequestUpdatedDto.quantity) }
        }
    }

    @Test
    @Order(6)
    fun `delete cteReceive`() {
        val cteReceiveId = addCte(cteReceiveRequestUpdatedDto).id
        mockMvc.delete("/api/v1/cte/receive/$cteReceiveId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}