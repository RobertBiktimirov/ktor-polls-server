package com.polls_example.feature.login.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class RegistrationDto(
    val email: String,
    val name: String,
    val password: String,
    val image: String? = null,
)

@Serializable
data class RefreshTokenDto(
    val token: String,
)

@Serializable
data class ConfirmEmailDto(
    val email: String,
    val code: Int,
)

@Serializable
data class EmailCodeDto(
    val email: String?,
)