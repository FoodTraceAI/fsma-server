// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.old

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
//    val userRepository: UserRepository,
//    val articleRepository: ArticleRepository
) {
//
//    @Test
//    fun `When findByIdOrNull then return Article`() {
//        val johnDoe = FsmaUser("johnDoe", "John", "Doe")
//        entityManager.persist(johnDoe)
//        val article = Article("Lorem", "Lorem", "dolor sit amet", johnDoe)
//        entityManager.persist(article)
//        entityManager.flush()
//        val found = articleRepository.findByIdOrNull(article.id)
//        assertThat(found).isEqualTo(article)
//    }
//
//    @Test
//    fun `When findByLogin then return User`() {
//        val johnDoe = FsmaUser("johnDoe", "John", "Doe")
//        entityManager.persist(johnDoe)
//        entityManager.flush()
//        val user = userRepository.findByLogin(johnDoe.login)
//        assertThat(user).isEqualTo(johnDoe)
//    }
}