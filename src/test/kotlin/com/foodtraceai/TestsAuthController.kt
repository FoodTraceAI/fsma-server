// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

import com.foodtraceai.auth.AuthLogin
import com.foodtraceai.model.FsmaUserDto
import com.foodtraceai.util.Role
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class TestsAuthController : TestsBase() {

    // TODO: add passwordreset token logic - we will need this
//    @Autowired
//    private lateinit var passwordResetTokenRepository: PasswordResetTokenRepository

//    @BeforeEach
//    fun before() = SetupFsmaTests.setup()
//    @BeforeAll

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
        val accessToken: String = authenticate(rootAuthLogin).first
        val rootId: Long = rootDto.id
        mockMvc.get("/api/v1/fsmauser/$rootId") {
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