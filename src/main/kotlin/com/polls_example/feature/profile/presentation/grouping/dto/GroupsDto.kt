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
    val name: String?,
    val imageUrl: String?
)

@Serializable
data class EditProfileModel(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
)