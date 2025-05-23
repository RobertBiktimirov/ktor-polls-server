package com.polls_example.feature.profile.presentation.grouping

import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.profile.data.GroupingRepository
import com.polls_example.feature.profile.domain.models.GroupInfoModel
import com.polls_example.feature.profile.presentation.grouping.dto.CreateGroupDto
import com.polls_example.feature.profile.presentation.grouping.dto.GroupInfoDto
import com.polls_example.feature.profile.presentation.grouping.dto.GroupsDto
import com.polls_example.feature.profile.presentation.grouping.dto.UserInGroupDto

class GroupingController(
    private val repository: GroupingRepository,
    private val userRepository: UserRepository,
) {
    suspend fun getGroupsByUserId(userId: Int): GroupsDto {
        val groups = repository.getGroups(userId)
        val dto = groups.groups.map { groupInfoModel ->
            getGroupInfoDto(groupInfoModel)
        }

        return GroupsDto(dto)
    }

    suspend fun getGroupById(userId: Int, groupId: Int): GroupInfoDto {
        val group = repository.getGroup(userId, groupId)
        return getGroupInfoDto(group)
    }

    suspend fun createGroup(userId: Int, createGroupDto: CreateGroupDto): GroupInfoDto {
        val groupInfo = repository.createGroup(userId, createGroupDto.name, createGroupDto.membersId)
        return getGroupInfoDto(groupInfo)
    }

    suspend fun deleteGroup(userId: Int, groupId: Int) {
        repository.deleteGroup(userId, groupId)
    }

    suspend fun addMemberInGroup(userId: Int, groupId: Int, memberId: Int) =
        repository.addMember(userId, groupId, memberId)

    suspend fun deleteMemberInGroup(userId: Int, groupId: Int, memberId: Int) =
        repository.deleteMember(groupId, memberId)

    suspend fun getUsersByEmail(userId: Int, email: String): List<UserInGroupDto> =
        userRepository.getUsersByEmail(userId, email).map {
            UserInGroupDto(
                it.id,
                it.email,
                it.name,
                it.image
            )
        }

    suspend fun getUserById(userId: Int): UserInGroupDto {
        val dto = userRepository.userById(userId)
        return UserInGroupDto(
            dto?.id ?: 0,
            dto?.email,
            dto?.name,
            dto?.image
        )
    }

    private suspend fun getGroupInfoDto(groupInfoModel: GroupInfoModel): GroupInfoDto {
        val members = getMembers(groupInfoModel)

        return GroupInfoDto(
            groupId = groupInfoModel.id,
            users = members,
            name = groupInfoModel.name
        )
    }

    private suspend fun getMembers(groupInfoModel: GroupInfoModel) =
        groupInfoModel.membersId.map { memberId ->
            val userInfo = userRepository.userById(memberId)
            UserInGroupDto(
                id = memberId,
                email = userInfo?.email,
                imageUrl = userInfo?.image,
                name = userInfo?.name
            )
        }
}