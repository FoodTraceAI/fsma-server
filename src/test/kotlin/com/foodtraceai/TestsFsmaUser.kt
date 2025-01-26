// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai

//@SpringBootTest
//@AutoConfigureMockMvc
//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
//class TestsFsmaUser : TestsBase() {
//
//    private lateinit var fsmaUserRequestDto: FsmaUserRequestDto
//    private lateinit var fsmaUserRequestDtoUpdated: FsmaUserRequestDto
//
//    @BeforeAll
//    fun localSetup() {
//
//        // -- Contact
//        fsmaUserRequestDto = FsmaUserRequestDto(
//            foodBusId = 1,
//            locationId = 1,
//            firstname = "firstname",
//            lastname = "lastname",
//            email = "firstname.lastname@gmail.com",
//            roles = listOf(Role.RootAdmin),
//        )
//        fsmaUserRequestDtoUpdated = FsmaUserRequestDto(
//            foodBusId = 1,
//            locationId = 1,
//            firstname = "Changedfirstname",
//            lastname = "Changedlastname",
//            email = "Changedfirstname.Changedlastname1@gmail.com",
//            phone = "+1 800 000 0000",
//            roles = listOf(Role.RootAdmin),
//        )
//    }
//
//    @AfterAll
//    fun teardown() {
//    }
//
//    // ------------------------------------------------------------------------
//
//    @Test
//    @Order(1)
//    fun `add FsmaUser`() {
//        val (accessToken, _) = authenticate(rootAuthLogin)
//        val contactId = 8   // DataLoader loads 7 contacts
//        val mvcResult = mockMvc.post("/api/v1/fsmauser") {
//            header("Authorization", "Bearer $accessToken")
//            content = objectMapper.writeValueAsString(fsmaUserRequestDto)
//            contentType = MediaType.APPLICATION_JSON
//        }.andExpect {
//            status { isCreated() }
//            content { contentType(MediaType.APPLICATION_JSON) }
//            jsonPath("$.id") { value(contactId) }
//            jsonPath("$.firstname") { value(fsmaUserRequestDto.firstname) }
//            jsonPath("$.lastname") { value(fsmaUserRequestDto.lastname) }
//            jsonPath("$.email") { value(fsmaUserRequestDto.email) }
//            jsonPath("$.phone") { value(fsmaUserRequestDto.phone) }
//        }.andReturn()
//        // val contactId: Long = JsonPath.read(mvcResult.response.contentAsString, "$.id")
//    }
//
//    @Test
//    @Order(2)
//    fun `get contact`() {
//        val foodBus = foodBusService.findById(fsmaUserRequestDto.foodBusId)
//        val location = locationService.findById(fsmaUserRequestDto.locationId)
//
//        val contactId = fsmaUserService.insert(0, fsmaUserRequestDto)).id
//        val (accessToken, _) = authenticate(rootAuthLogin)
//        mockMvc.get("/api/v1/contact/$contactId") {
//            header("Authorization", "Bearer $accessToken")
//        }.andExpect {
//            status { isOk() }
//            content { contentType(MediaType.APPLICATION_JSON) }
//            jsonPath("$.id") { value(contactId) }
//            jsonPath("$.firstname") { value(fsmaUserRequestDto.firstname) }
//            jsonPath("$.lastname") { value(fsmaUserRequestDto.lastname) }
//            jsonPath("$.email") { value(fsmaUserRequestDto.email) }
//            jsonPath("$.phone") { value(fsmaUserRequestDto.phone) }
//        }
//    }
//
//    @Test
//    @Order(3)
//    fun `update contact`() {
//        val contactId = contactService.insert(fsmaUserRequestDto.toContact()).id
//        val (accessToken, _) = authenticate(rootAuthLogin)
//        mockMvc.put("/api/v1/contact/$contactId") {
//            header("Authorization", "Bearer $accessToken")
//            content = objectMapper.writeValueAsString(fsmaUserRequestDtoUpdated)
//            contentType = MediaType.APPLICATION_JSON
//        }.andExpect {
//            status { isOk() }
//            content { contentType(MediaType.APPLICATION_JSON) }
//            jsonPath("$.id") { value(contactId) }
//            jsonPath("$.firstname") { value(fsmaUserRequestDtoUpdated.firstname) }
//            jsonPath("$.lastname") { value(fsmaUserRequestDtoUpdated.lastname) }
//            jsonPath("$.email") { value(fsmaUserRequestDtoUpdated.email) }
//            jsonPath("$.phone") { value(fsmaUserRequestDtoUpdated.phone) }
//        }
//    }
//
//    @Test
//    @Order(4)
//    fun `delete contact`() {
//        val contactId = contactService.insert(fsmaUserRequestDto.toContact(0)).id
//        val (accessToken, _) = authenticate(rootAuthLogin)
//        mockMvc.delete("/api/v1/contact/$contactId") {
//            header("Authorization", "Bearer $accessToken")
//        }.andExpect {
//            status { isNoContent() }
//        }
//    }
//}
