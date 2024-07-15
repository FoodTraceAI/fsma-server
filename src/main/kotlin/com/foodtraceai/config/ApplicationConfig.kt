// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.config

import com.foodtraceai.model.FsmaUser
import com.foodtraceai.repository.FsmaUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.OffsetDateTime
import java.util.*

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@Suppress("MagicNumber")
class ApplicationConfig {

    @Autowired
    protected lateinit var repository: FsmaUserRepository

    @Bean
    fun dateTimeProvider(): DateTimeProvider {
        return DateTimeProvider {
            Optional.of(OffsetDateTime.now())
        }
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username: String ->
            repository.findByEmailIgnoreCase(username) ?: throw UsernameNotFoundException("User $username not found")
        }
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override
            fun addCorsMappings(registry: CorsRegistry) {
                registry // (CorsRegistry)
                    .addMapping("/**") // any path (CorsRegistration)
                    .allowedOrigins("*") // any domain
                    .allowedMethods("*") // any method
                    .allowedHeaders("*") // any header
                    .maxAge(1800) // 30 minutes
            }
        }
    }

    @Bean
    fun auditorProvider(): AuditorAware<FsmaUser> {
        return CustomAuditorAware()
    }

    @Bean
    fun threadPoolTaskExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.maxPoolSize = 100
        executor.queueCapacity = 50
        executor.setThreadNamePrefix("async-")
        return executor
    }

    @Bean
    fun taskExecutor(threadPoolTaskExecutor: ThreadPoolTaskExecutor?): DelegatingSecurityContextAsyncTaskExecutor {
        return DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor)
    }
}

private class CustomAuditorAware : AuditorAware<FsmaUser> {
    override fun getCurrentAuditor(): Optional<FsmaUser> {
        return if (SecurityContextHolder.getContext().authentication != null) {
            try {
                val auth = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
                val loggedInUser = auth.principal as FsmaUser
                Optional.of(loggedInUser)
            } catch (e: ClassCastException) {
                // AnonymousAuthenticationToken can't be cast to UsernamePasswordAuthenticationToken
                // AnonymousAuthenticationToken is the authentication type when there is no logged in user
                Optional.empty()
            }
        } else {
            Optional.empty()
        }
    }
}