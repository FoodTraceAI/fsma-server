// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.auth

import com.foodtraceai.controller.BaseController
import com.foodtraceai.model.FsmaUser
import com.foodtraceai.model.FsmaUserDto
import com.foodtraceai.util.AuthorizationException
import com.foodtraceai.util.UnauthorizedRequestException
import com.foodtraceai.util.maxRole
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private const val LOGIN_BASE_URL = "/api/v1/auth"

@RestController
@RequestMapping(value = [LOGIN_BASE_URL])
class AuthController : BaseController() {

    @Autowired
    private lateinit var authService: AuthService

    @PostMapping("/login")
    fun login(@RequestBody authLogin: AuthLogin?): ResponseEntity<AuthResponse> {
        val loginResponse = when {
            authLogin?.refreshToken != null ->
                authService.refreshAccessToken(authLogin.refreshToken)

            authLogin?.email != null && authLogin.password != null ->
                authService.authenticate(authLogin.email, authLogin.password)

            else -> throw AuthorizationException("Invalid user credentials")
        }
        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("/switch-to/{fsmaUserId}")
    fun switchTo(
        @RequestHeader(HttpHeaders.AUTHORIZATION) auth: String,
        @PathVariable(value = "fsmaUserId") fsmaUserId: Long
    ): ResponseEntity<AuthResponse> {
        if (auth.startsWith("Bearer ")) {
            val jwt = auth.substring("Bearer ".length)
            val userEmail = jwtService.extractUsername(jwt)

            val fsaUser = fsmaUserService.findByEmailIgnoreCase(userEmail)
            val switchToFsmaUser = fsmaUserService.findById(fsmaUserId)

            if (allowedToSwitchRootAdminToFoodBusinessAdmin(fsaUser, switchToFsmaUser)) {
                return ResponseEntity.ok(authService.authResponse(switchToFsmaUser!!.email))
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    private fun allowedToSwitchRootAdminToFoodBusinessAdmin(
        fsmaUser: FsmaUser?,
        switchToFsmaUser: FsmaUser?
    ): Boolean {
        if (fsmaUser == null || switchToFsmaUser == null) {
            return false
        }

        return switchToFsmaUser.isFoodBusAdmin() && fsmaUser.isRootAdmin()
    }

    //TODO: Remove
//    private fun allowedToSwitchParentResellerAdminToResellerAdmin(
//        fsmaUser: FsmaUser?,
//        switchToFsaUser: FsmaUser?
//    ): Boolean {
//        if (fsmaUser == null || switchToFsaUser == null) {
//            return false
//        }
//
//        return switchToFsaUser.isResellerAdmin() &&
//                fsmaUser.isParentResellerAdmin() &&
//                (switchToFsaUser.client.reseller.parentReseller?.id == fsmaUser.resellerId)
//    }
//
//    private fun allowedToSwitchResellerAdminToClientAdmin(fsaUser: FsaUser?, switchToFsaUser: FsaUser?): Boolean {
//        if (fsaUser == null || switchToFsaUser == null) {
//            return false
//        }
//
//        return switchToFsaUser.isClientAdmin() &&
//                fsaUser.isResellerAdmin() &&
//                (switchToFsaUser.client.reseller.id == fsaUser.resellerId)
//    }

    @PostMapping("/register")
    @SecurityRequirement(name = "bearerAuth")
    fun createNewFsaUser(
        @AuthenticationPrincipal fsmaUser: FsmaUser,
        @Valid @RequestBody newUserDto: FsmaUserDto
    ): ResponseEntity<AuthResponse> {
        // Make sure user has necessary permission
        // TODO: remove
//        if (maxRole(fsaUser.roles) < Role.ClientAdmin)
//            throw UnauthorizedRequestException("FsaUser insufficient permissions id=${fsaUser.id}")

        if (maxRole(fsmaUser.roles) < maxRole(newUserDto.roles))
            throw UnauthorizedRequestException("FsmaUser cannot create role with higher permissions id=${newUserDto.id}")

        assertFoodBusinessMatchesToken(fsmaUser, newUserDto.foodBusId)
        return ResponseEntity.ok(authService.createNewFsmaUser(newUserDto))
    }

//    @PostMapping("/reset-password")
//    fun resetPassword(
//        @RequestBody resetPasswordRequestDto: ResetPasswordRequestDto
//    ): ResponseEntity<NotifyResponse> {
//        val fsaUser = fsaUserService.findByEmailIgnoreCase(resetPasswordRequestDto.email)
//            ?: throw EntityNotFoundException("FsaUser not found: ${resetPasswordRequestDto.email}")
//
//        // request for password reset email
//        val passwordResetToken = authService.createPasswordResetTokenForUser(fsaUser)
//        val notifyResponse = authService.requestSendPasswordResetEmail(
//            token = passwordResetToken.token,
//            email = passwordResetToken.fsaUser.email
//        )
//
//        return ResponseEntity.ok().body(notifyResponse)
//    }
//
//    @PostMapping("/change-password")
//    fun changePassword(
//        @RequestBody changePasswordDto: ChangePasswordDto
//    ): ResponseEntity<String> {
//        authService.changeUserPassword(
//            passwordResetToken = changePasswordDto.passwordResetToken,
//            newPassword = changePasswordDto.newPassword
//        )
//
//        return ResponseEntity.ok().body("Password successfully changed")
//    }
}
