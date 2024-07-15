// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.config

import com.foodtraceai.auth.JwtAuthenticationEntryPoint
import com.foodtraceai.auth.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

private const val ACTUATOR_ROLE = "ACTUATOR"

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfiguration {

    @Autowired
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

    @Autowired
    private lateinit var jwtAuthFilter: JwtAuthenticationFilter

    @Autowired
    private lateinit var authenticationProvider: AuthenticationProvider

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Value("\${actuator.admin.username:admin}")
    private val actuatorUsername: String? = null

    @Value("\${actuator.admin.password:password}")
    private val actuatorPassword: String? = null

    @Bean
    @Throws(java.lang.Exception::class)
    fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )

        authenticationManagerBuilder.inMemoryAuthentication()
            .withUser(actuatorUsername)
            .password(passwordEncoder.encode(actuatorPassword))
            .roles(ACTUATOR_ROLE)

        authenticationManagerBuilder.authenticationProvider(authenticationProvider)

        return authenticationManagerBuilder.build()
    }

    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/api/**")
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authz ->
                authz.requestMatchers(
                    "/api/v1/auth/**", // login & register
                    "/api/v1/reseller/domain/**",
                    "/api/v1/equipment/position",
                    "/api/v1/quickbooks/login", // Quickbooks login
                    "/api/v1/quickbooks/oauth2callback", // Quickbooks login callback
                    "/api/v1/estimate-tracking/**",
                    "/api/v1/sheet",
                    "/api/v1/sheet/**",
                )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    @Order(2)
    @Throws(Exception::class)
    fun actuatorSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/actuator/**")
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authz ->
                authz.requestMatchers(
                    "/actuator/health",
                )
                    .permitAll()
                    .requestMatchers(EndpointRequest.toAnyEndpoint())
                    .hasRole(ACTUATOR_ROLE)
                    .anyRequest()
                    .authenticated()
            }
            .authenticationManager(authManager(http))
            .httpBasic(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authz ->
                authz.requestMatchers(
                    "/",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
        return http.build()
    }
}