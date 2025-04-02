package com.polls_example.feature.files.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserAvatarDto(
    val path: String
)
