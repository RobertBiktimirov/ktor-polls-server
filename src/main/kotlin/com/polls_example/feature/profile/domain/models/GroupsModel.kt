package com.polls_example.feature.profile.domain.models

data class GroupsModel(
    val groups: List<GroupInfoModel>
)

data class GroupInfoModel(
    val id: Int,
    val name: String,
    val membersId: List<Int>,
)