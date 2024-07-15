// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.old

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

//    @BeforeAll
//    fun setup() {
//        println(">> Setup")
//    }
//
//    @Test
//    fun `Assert blog page title, content and status code`() {
//        println(">> Assert blog page title, content and status code")
//        val entity = restTemplate.getForEntity<String>("/")
//        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(entity.body).contains("<h1>FSMA</h1>", "Lorem")
//    }
//
//    @Test
//    fun `Assert article page title, content and status code`() {
//        println(">> Assert article page title, content and status code")
//        val title = "Lorem"
//        val entity = restTemplate.getForEntity<String>("/article/${title.toSlug()}")
//        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(entity.body).contains(title, "Lorem", "dolor sit amet")
//    }
//
//    @AfterAll
//    fun teardown() {
//        println(">> Tear down")
//    }

}