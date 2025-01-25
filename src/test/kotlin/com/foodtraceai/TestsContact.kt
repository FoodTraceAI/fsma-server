// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.model.ContactRequestDto
import com.foodtraceai.model.toContact
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
class TestsContact : TestsBase() {

    private lateinit var contactRequestDto: ContactRequestDto
    private lateinit var contactRequestDtoUpdated: ContactRequestDto

    @BeforeAll
    fun localSetup() {

        // -- Contact
        contactRequestDto = ContactRequestDto(
            firstName = "firstName",
            lastName = "lastName",
            email = "firstName.lastName1@gmail.com",
            phone = "+1 800 555 1212",
        )
        contactRequestDtoUpdated = ContactRequestDto(
            firstName = "ChangedfirstName",
            lastName = "ChangedlastName",
            email = "firstName1.lastName1@gmail.com",
            phone = "+1 800 000 0000",
        )
    }

    @AfterAll
    fun teardown() {
    }

    // ------------------------------------------------------------------------

    @Test
    @Order(1)
    fun `add contact`() {
        val (accessToken, _) = authenticate(rootAuthLogin)
        val contactId = 8   // DataLoader loads 7 contacts
        val mvcResult = mockMvc.post("/api/v1/contact") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(contactRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(contactId) }
            jsonPath("$.firstName") { value(contactRequestDto.firstName) }
            jsonPath("$.lastName") { value(contactRequestDto.lastName) }
            jsonPath("$.email") { value(contactRequestDto.email) }
            jsonPath("$.phone") { value(contactRequestDto.phone) }
        }.andReturn()
        // val contactId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
    }

    @Test
    @Order(2)
    fun `get contact`() {
        val contactId = contactService.insert(contactRequestDto.toContact(0)).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.get("/api/v1/contact/$contactId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(contactId) }
            jsonPath("$.firstName") { value(contactRequestDto.firstName) }
            jsonPath("$.lastName") { value(contactRequestDto.lastName) }
            jsonPath("$.email") { value(contactRequestDto.email) }
            jsonPath("$.phone") { value(contactRequestDto.phone) }
        }
    }

    @Test
    @Order(3)
    fun `update contact`() {
        val contactId = contactService.insert(contactRequestDto.toContact()).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.put("/api/v1/contact/$contactId") {
            header("Authorization", "Bearer $accessToken")
            content = objectMapper.writeValueAsString(contactRequestDtoUpdated)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(contactId) }
            jsonPath("$.firstName") { value(contactRequestDtoUpdated.firstName) }
            jsonPath("$.lastName") { value(contactRequestDtoUpdated.lastName) }
            jsonPath("$.email") { value(contactRequestDtoUpdated.email) }
            jsonPath("$.phone") { value(contactRequestDtoUpdated.phone) }
        }
    }

    @Test
    @Order(4)
    fun `delete contact`() {
        val contactId = contactService.insert(contactRequestDto.toContact(0)).id
        val (accessToken, _) = authenticate(rootAuthLogin)
        mockMvc.delete("/api/v1/contact/$contactId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }
}
