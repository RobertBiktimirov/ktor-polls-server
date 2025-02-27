package com.polls_example.feature.profile.presentation.grouping.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupDto(
    val name: String,
    val membersId: List<Int>,
)
