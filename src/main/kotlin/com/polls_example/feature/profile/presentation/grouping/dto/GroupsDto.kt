package com.polls_example.feature.profile.presentation.grouping.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupsDto(
    val groups: List<GroupInfoDto>
)

@Serializable
data class GroupInfoDto(
    val groupId: Int,
    val name: String,
    val users: List<UserInGroupDto>,
)

@Serializable
data class UserInGroupDto(
    val id: Int,
    val email: String?,
    val imageUrl: String?
)