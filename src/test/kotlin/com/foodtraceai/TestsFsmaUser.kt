// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.FsmaUserRequestDto
import com.foodtraceai.model.toFsmaUser
import com.foodtraceai.util.Role
import jakarta.persistence.EntityNotFoundException
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
class TestsFsmaUser : TestsBase() {

    private lateinit var fsmaUser1RequestDto: FsmaUserRequestDto
    private lateinit var fsmaUser2RequestDto: FsmaUserRequestDto
    private lateinit var fsmaUser2RequestDtoUpdated: FsmaUserRequestDto
    private lateinit var accessToken: String
    private var fsmaUser2Id: Long=0

    @BeforeAll
    fun localSetup() {
        accessToken = authenticate(rootAuthLogin).first

        // -- Contact
        fsmaUser1RequestDto = FsmaUserRequestDto(
            foodBusId = 1,
            locationId = 1,
            firstname = "firstname",
            lastname = "lastname",
            email = "firstname.lastname@gmail.com",
            roles = listOf(Role.RootAdmin),
        )
        fsmaUser2RequestDto = FsmaUserRequestDto(
            foodBusId = 2,
            locationId = 2,
            firstname = "2ndfirstname",
            lastname = "2ndlastname",
            email = "2ndfirstname.2ndlastname@gmail.com",
            roles = listOf(Role.RootAdmin),
        )
        fsmaUser2RequestDtoUpdated = FsmaUserRequestDto(
            foodBusId = 1,
            locationId = 1,
            firstname = "Changedfirstname",
            lastname = "Changedlastname",
            email = "Changedfirstname.Changedlastname1@gmail.com",
            phone = "+1 800 000 0000",
            roles = listOf(Role.RootAdmin),
        )
    }

    @AfterAll
    fun teardown() {
    }

    private fun addFsmaUser(dto: FsmaUserRequestDto): FsmaUser {
        val foodBus = foodBusService.findById(dto.foodBusId)
            ?: throw com.foodtraceai.util.EntityNotFoundException("FoodBus not found: ${dto.foodBusId}")
        val location = locationService.findById(dto.locationId)
            ?: throw EntityNotFoundException("CteReceive locationId: ${dto.locationId} not found")
        val fsmaUs = dto.toFsmaUser(id = 0, foodBus, location)
        return fsmaUserService.insert(fsmaUs)
    }

    // ------------------------------------------------------------------------

    @Test
    @Order(1)
    fun `add FsmaUser`() {
        val fsmaUserId = 7   // DataLoader loads 6 contacts
        val mvcResult = mockMvc.post("/api/v1/fsmauser") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(fsmaUser1RequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(fsmaUserId) }
            jsonPath("$.firstname") { value(fsmaUser1RequestDto.firstname) }
            jsonPath("$.lastname") { value(fsmaUser1RequestDto.lastname) }
            jsonPath("$.email") { value(fsmaUser1RequestDto.email) }
            jsonPath("$.phone") { value(fsmaUser1RequestDto.phone) }
        }.andReturn()
        // val contactId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    @Order(2)
    fun `get FsmaUser`() {
        fsmaUser2Id = addFsmaUser(fsmaUser2RequestDto).id
        mockMvc.get("/api/v1/fsmauser/$fsmaUser2Id") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(fsmaUser2Id) }
            jsonPath("$.firstname") { value(fsmaUser2RequestDto.firstname) }
            jsonPath("$.lastname") { value(fsmaUser2RequestDto.lastname) }
            jsonPath("$.email") { value(fsmaUser2RequestDto.email) }
            jsonPath("$.phone") { value(fsmaUser2RequestDto.phone) }
        }
    }

    @Test
    @Order(3)
    fun `update FsmaUser`() {
        mockMvc.put("/api/v1/fsmauser/$fsmaUser2Id") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(fsmaUser2RequestDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(fsmaUser2Id) }
            jsonPath("$.firstname") { value(fsmaUser2RequestDtoUpdated.firstname) }
            jsonPath("$.lastname") { value(fsmaUser2RequestDtoUpdated.lastname) }
            jsonPath("$.email") { value(fsmaUser2RequestDtoUpdated.email) }
            jsonPath("$.phone") { value(fsmaUser2RequestDtoUpdated.phone) }
        }
    }

    @Test
    @Order(4)
    fun `delete FsmaUser`() {
        mockMvc.delete("/api/v1/fsmauser/$fsmaUser2Id") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}
