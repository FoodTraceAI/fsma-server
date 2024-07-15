// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.fasterxml.jackson.databind.ObjectMapper
import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.auth.AuthService
import com.foodtraceai.model.FsmaUserDto
import com.foodtraceai.service.FsmaUserService
import com.foodtraceai.util.Role
import com.jayway.jsonpath.JsonPath
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class TestsAuthController {
    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var fsmaUserService: FsmaUserService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // TODO: add passwordreset token logic - we will need this
//    @Autowired
//    private lateinit var passwordResetTokenRepository: PasswordResetTokenRepository

//    @BeforeEach
//    fun before() = SetupFsmaTests.setup()
//    @BeforeAll

    fun authenticate(authLogin: AuthLogin): List<String> {
        val mvcResult = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(authLogin)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()

        return listOf(
            JsonPath.read(mvcResult.response.contentAsString, "$.accessToken"),
            JsonPath.read(mvcResult.response.contentAsString, "$.refreshToken"),
        )
    }

    @Test
    fun `get root admin`() {
        val rootDto = FsmaUserDto(
            id = 1,
            foodBusId = 1,//foodBusinessList[0].id,
            locationId = 1,
            email = "root@foodtraceai.com",
            password = "123",
            roles = listOf(Role.RootAdmin),
            firstname = "Root",
            lastname = "Root",
        )
        val rootAuthLogin = AuthLogin(email = rootDto.email, password = rootDto.password, refreshToken = null)
        val accessToken: String = authenticate(rootAuthLogin)[0]
        val rootId: Long = rootDto.id
        mockMvc.get("/api/v1/fsauser/$rootId") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            //jsonPath("$.id") { value(rootId) }
            jsonPath("$.firstname") { value(rootDto.firstname) }
            jsonPath("$.lastname") { value(rootDto.lastname) }
            jsonPath("$.email") { equalToIgnoringCase(rootDto.email) }
            jsonPath("$.notes") { value(rootDto.notes) }
            // TODO: Ask Milo how ot fix this comparison
            // jsonPath("$.roles") { value(rootAdminDto.roles) }
            jsonPath("$.phone") { value(rootDto.phone) }
        }
    }
}