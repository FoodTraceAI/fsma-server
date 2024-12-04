// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.model.*
import com.foodtraceai.service.*
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
class TestsFoodBus {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // ------------------------------------------------------------------------
    // Test setup

    @Autowired
    private lateinit var addressService: AddressService

    @Autowired
    private lateinit var contactService: ContactService

    @Autowired
    private lateinit var franchisorService: FranchisorService

    @Autowired
    private lateinit var foodBusService: FoodBusService

    @Autowired
    private lateinit var resellerService: ResellerService

    private lateinit var foodBusDto: FoodBusDto
    private lateinit var foodBusDtoUpdated: FoodBusDto

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
        foodBusDto = FoodBusDto(
            id = 0,
            resellerId = 1,
            mainAddressId = 1,
            foodBusContactId = 1,
            foodBusName = "Fred's Restaurant",
            foodBusDesc = "Restaurant",
            franchisorId = null,
        )

        foodBusDtoUpdated = FoodBusDto(
            id = 0,
            resellerId = 1,
            mainAddressId = 1,
            foodBusContactId = 1,
            foodBusName = "Steve's Restaurant",
            foodBusDesc = "RFE",
            franchisorId = null,
        )
    }


    // ------------------------------------------------------------------------

    private fun addFoodBus(dto: FoodBusDto): FoodBus {
        val reseller: Reseller? = dto.resellerId?.let {
            resellerService.findById(it)
                ?: throw EntityNotFoundException("Reseller not found = ${dto.resellerId}")
        }

        val mainAddress = addressService.findById(dto.mainAddressId)
            ?: throw EntityNotFoundException("FoodBus addresssId not found = ${foodBusDto.mainAddressId}")

        val franchisor: Franchisor? = foodBusDto.franchisorId?.let {
            franchisorService.findById(it)
                ?: throw EntityNotFoundException("Franchisor not found = $it")
        }

        val foodBusContact = contactService.findById(foodBusDto.foodBusContactId)
            ?: throw EntityNotFoundException("Contact not found = ${foodBusDto.foodBusContactId}")

        return foodBusService.insert(foodBusDto.toFoodBus(reseller, mainAddress, foodBusContact, franchisor))
    }

    @Test
    fun `add foodBus`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.post("/api/v1/foodbus") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(foodBusDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.foodBusContactId") { value(foodBusDto.foodBusContactId) }
            jsonPath("$.foodBusName") { value("Fred's Restaurant") }
            jsonPath("$.foodBusDesc") { value("Restaurant") }
        }
        //val foodBusId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    fun `get foodBus`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val foodBusId = addFoodBus(foodBusDto).id
        mockMvc.get("/api/v1/foodbus/$foodBusId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(foodBusId) }
            jsonPath("$.foodBusContactId") { value(foodBusDto.foodBusContactId) }
            jsonPath("$.foodBusName") { value("Fred's Restaurant") }
            jsonPath("$.foodBusDesc") { value("Restaurant") }
        }
    }

    @Test
    fun `update foodBusiness`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val foodBusId: Long = addFoodBus(foodBusDto).id
        foodBusDtoUpdated = foodBusDtoUpdated.copy(id = foodBusId)
        mockMvc.put("/api/v1/foodbus/$foodBusId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(foodBusDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(foodBusId) }
            jsonPath("$.foodBusContactId") { value(foodBusDtoUpdated.foodBusContactId) }
            jsonPath("$.foodBusName") { value(foodBusDtoUpdated.foodBusName) }
            jsonPath("$.foodBusDesc") { value(foodBusDtoUpdated.foodBusDesc) }
        }
    }

    @Test
    fun `delete business`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val foodBusId: Long = addFoodBus(foodBusDto).id
        mockMvc.delete("/api/v1/foodbus/$foodBusId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}