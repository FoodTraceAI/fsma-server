// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.controller.cte.CteShipController
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsCteShip:TestsBase() {
    private lateinit var cteShipArgs: CteShipController.CteShipArgs

    @BeforeEach
    fun localSetup() {
        cteShipArgs = CteShipController.CteShipArgs(
            ftlItem = FtlItem.Cucumbers,
            tlcId = 1,
            quantity = 100,
            unitOfMeasure = UnitOfMeasure.Case,
            prodDesc = "Cucumbers",
            shipToLocationId = 1,
            locationId = 2,
            referenceDocumentType = ReferenceDocumentType.ASN,
            referenceDocumentNum = "refDocNum",
        )

    }

    @AfterAll
    fun teardown() {
    }

    // ------------------------------------------------------------------------

    @Test
    fun `make CteShip for CteShipArgs`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.post("/api/v1/cte/ship/makeCteShip") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(cteShipArgs)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.ftlItem") { value(cteShipArgs.ftlItem.name) }
            jsonPath("$.quantity") { value(cteShipArgs.quantity) }
            jsonPath("$.prodDesc") { value(cteShipArgs.prodDesc) }
        }
    }
}