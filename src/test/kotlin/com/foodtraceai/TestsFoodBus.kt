// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.*
import com.foodtraceai.util.EntityNotFoundException
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
class TestsFoodBus : TestsBase() {
    private lateinit var foodBusRequestDto: FoodBusRequestDto
    private lateinit var foodBusRequestDtoUpdated: FoodBusRequestDto

    @BeforeEach
    fun localSetup() {
        foodBusRequestDto = FoodBusRequestDto(
            resellerId = 1,
            mainAddressId = 1,
            foodBusContactId = 1,
            foodBusName = "Fred's Restaurant",
            foodBusDesc = "Restaurant",
            franchisorId = null,
        )

        foodBusRequestDtoUpdated = FoodBusRequestDto(
            resellerId = 1,
            mainAddressId = 1,
            foodBusContactId = 1,
            foodBusName = "Steve's Restaurant",
            foodBusDesc = "RFE",
            franchisorId = null,
        )
    }

    @AfterAll
    fun teardown() {
    }

    // ------------------------------------------------------------------------

    private fun addFoodBus(dto: FoodBusRequestDto): FoodBus {
        val reseller: Reseller? = dto.resellerId?.let {
            resellerService.findById(it)
                ?: throw EntityNotFoundException("Reseller not found = ${dto.resellerId}")
        }

        val mainAddress = addressService.findById(dto.mainAddressId)
            ?: throw EntityNotFoundException("FoodBus addresssId not found = ${foodBusRequestDto.mainAddressId}")

        val franchisor: Franchisor? = foodBusRequestDto.franchisorId?.let {
            franchisorService.findById(it)
                ?: throw EntityNotFoundException("Franchisor not found = $it")
        }

        val foodBusContact = contactService.findById(foodBusRequestDto.foodBusContactId)
            ?: throw EntityNotFoundException("Contact not found = ${foodBusRequestDto.foodBusContactId}")

        return foodBusService.insert(
            foodBusRequestDto.toFoodBus(
                id = 0,
                reseller,
                mainAddress,
                foodBusContact,
                franchisor
            )
        )
    }

    @Test
    @Order(1)
    fun `add foodBus`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.post("/api/v1/foodbus") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(foodBusRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.foodBusContactId") { value(foodBusRequestDto.foodBusContactId) }
            jsonPath("$.foodBusName") { value("Fred's Restaurant") }
            jsonPath("$.foodBusDesc") { value("Restaurant") }
        }
        //val foodBusId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    @Order(2)
    fun `get foodBus`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val foodBusId = addFoodBus(foodBusRequestDto).id
        mockMvc.get("/api/v1/foodbus/$foodBusId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(foodBusId) }
            jsonPath("$.foodBusContactId") { value(foodBusRequestDto.foodBusContactId) }
            jsonPath("$.foodBusName") { value("Fred's Restaurant") }
            jsonPath("$.foodBusDesc") { value("Restaurant") }
        }
    }

    @Test
    @Order(3)
    fun `update foodBusiness`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val foodBusId: Long = addFoodBus(foodBusRequestDto).id
        mockMvc.put("/api/v1/foodbus/$foodBusId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(foodBusRequestDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(foodBusId) }
            jsonPath("$.foodBusContactId") { value(foodBusRequestDtoUpdated.foodBusContactId) }
            jsonPath("$.foodBusName") { value(foodBusRequestDtoUpdated.foodBusName) }
            jsonPath("$.foodBusDesc") { value(foodBusRequestDtoUpdated.foodBusDesc) }
        }
    }

    @Test
    @Order(4)
    fun `delete business`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val foodBusId: Long = addFoodBus(foodBusRequestDto).id
        mockMvc.delete("/api/v1/foodbus/$foodBusId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}