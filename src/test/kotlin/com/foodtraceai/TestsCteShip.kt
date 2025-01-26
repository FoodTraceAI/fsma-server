// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.cte.CteShip
import com.foodtraceai.model.cte.CteShipRequestDto
import com.foodtraceai.model.cte.toCteShip
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
class TestsCteShip : TestsBase() {
    private lateinit var cteShipRequestDto: CteShipRequestDto
    private lateinit var cteShipRequestUpdatedDto: CteShipRequestDto
    private lateinit var accessToken: String

    @BeforeEach
    fun localSetup() {
        accessToken = authenticate(rootAuthLogin).first

        cteShipRequestDto = CteShipRequestDto(
            ftlItem = FtlItem.Cucumbers,
            tlcId = 1,
            quantity = 100,
            unitOfMeasure = UnitOfMeasure.Case,
            prodDesc = "Cucumbers",
            variety = "Green Cucumbers",
            shipToLocationId = 3,
            locationId = 2,
            shipDate = LocalDate.now(),
            shipTime = OffsetDateTime.now(),
            tlcSourceId = 1,
            referenceDocumentType = ReferenceDocumentType.BOL,
            referenceDocumentNum = "refDocNum",
        )

        cteShipRequestUpdatedDto = CteShipRequestDto(
            ftlItem = FtlItem.Cucumbers,
            tlcId = 1,
            quantity = 200,
            unitOfMeasure = UnitOfMeasure.Kilo,
            prodDesc = "Updated Cucumbers",
            variety = "Red Cucumbers",
            shipToLocationId = 3,
            locationId = 2,
            shipDate = LocalDate.now(),
            shipTime = OffsetDateTime.now(),
            tlcSourceId = 1,
            referenceDocumentType = ReferenceDocumentType.ASN,
            referenceDocumentNum = "refDocNum",
        )
    }

    @AfterAll
    fun teardown() {
    }

    private fun addCte(dto: CteShipRequestDto): CteShip {
        val tlc = traceLotCodeService.findById(dto.tlcId)
            ?: throw EntityNotFoundException("CteReceive tlcId: ${dto.tlcId} not found")
        val shipToLocation = locationService.findById(dto.shipToLocationId)
            ?: throw EntityNotFoundException("CteReceive shipTolocationId: ${dto.locationId} not found")
        val location = locationService.findById(dto.locationId)
            ?: throw EntityNotFoundException("CteReceive locationId: ${dto.locationId} not found")
        val tlcSource = locationService.findById(dto.tlcSourceId)
            ?: throw EntityNotFoundException("CteReceive tlcSourceId: ${dto.tlcSourceId} not found")
        return cteShipService.insert(
            dto.toCteShip(id = 0, tlc, shipToLocation, location, tlcSource)
        )
    }

    // ------------------------------------------------------------------------
    @Test
    @Order(1)
    fun `add cteShip`() {
        val cteShipId = 1   // DataLoader loads 0 shipping ctes
        val mvcResult = mockMvc.post("/api/v1/cte/ship") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(cteShipRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(cteShipId) }
            jsonPath("$.ftlItem") { value(cteShipRequestDto.ftlItem.name) }
            jsonPath("$.unitOfMeasure") { value(cteShipRequestDto.unitOfMeasure.name) }
            jsonPath("$.prodDesc") { value(cteShipRequestDto.prodDesc) }
            jsonPath("$.variety") { value(cteShipRequestDto.variety) }
            jsonPath("$.tlcId") { value(cteShipRequestDto.tlcId) }
            jsonPath("$.quantity") { value(cteShipRequestDto.quantity) }
        }.andReturn()
        // val contactId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    @Order(2)
    fun `get cteShip`() {
        val cteShipId = addCte(cteShipRequestDto).id
        mockMvc.get("/api/v1/cte/ship/$cteShipId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(cteShipId) }
            jsonPath("$.ftlItem") { value(cteShipRequestDto.ftlItem.name) }
            jsonPath("$.unitOfMeasure") { value(cteShipRequestDto.unitOfMeasure.name) }
            jsonPath("$.prodDesc") { value(cteShipRequestDto.prodDesc) }
            jsonPath("$.variety") { value(cteShipRequestDto.variety) }
            jsonPath("$.tlcId") { value(cteShipRequestDto.tlcId) }
            jsonPath("$.quantity") { value(cteShipRequestDto.quantity) }
        }
    }

    @Test
    @Order(3)
    fun `update cteShip`() {
        val cteShipId = addCte(cteShipRequestUpdatedDto).id
        mockMvc.put("/api/v1/cte/ship/$cteShipId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(cteShipRequestUpdatedDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(cteShipId) }
            jsonPath("$.ftlItem") { value(cteShipRequestUpdatedDto.ftlItem.name) }
            jsonPath("$.unitOfMeasure") { value(cteShipRequestUpdatedDto.unitOfMeasure.name) }
            jsonPath("$.prodDesc") { value(cteShipRequestUpdatedDto.prodDesc) }
            jsonPath("$.variety") { value(cteShipRequestUpdatedDto.variety) }
            jsonPath("$.tlcId") { value(cteShipRequestUpdatedDto.tlcId) }
            jsonPath("$.quantity") { value(cteShipRequestUpdatedDto.quantity) }
        }
    }

    @Test
    @Order(4)
    fun `delete cteShip`() {
        val cteShipId = addCte(cteShipRequestUpdatedDto).id
        mockMvc.delete("/api/v1/cte/ship/$cteShipId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }


//    @Test
//    fun `make CteShip for CteShipArgs`() {
//        val (accessToken, _) = authenticate(rootAuthLogin)
//        mockMvc.post("/api/v1/cte/ship/makeCteShip") {
//            header("Authorization", "Bearer $accessToken")
//            content = objectMapper.writeValueAsString(cteShipArgs)
//            contentType = MediaType.APPLICATION_JSON
//        }.andExpect {
//            status { isCreated() }
//            content { contentType(MediaType.APPLICATION_JSON) }
//            jsonPath("$.ftlItem") { value(cteShipArgs.ftlItem.name) }
//            jsonPath("$.quantity") { value(cteShipArgs.quantity) }
//            jsonPath("$.prodDesc") { value(cteShipArgs.prodDesc) }
//        }
//    }
}