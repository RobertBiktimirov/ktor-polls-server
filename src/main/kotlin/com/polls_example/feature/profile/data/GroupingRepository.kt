package com.polls_example.feature.profile.data

import com.polls_example.database.GroupItemsDAO
import com.polls_example.database.GroupItemsTable
import com.polls_example.database.GroupsDAO
import com.polls_example.database.GroupsTable
import com.polls_example.feature.profile.domain.exceptions.GroupException403
import com.polls_example.feature.profile.domain.exceptions.GroupException404
import com.polls_example.feature.profile.domain.exceptions.GroupSampleException
import com.polls_example.feature.profile.domain.models.GroupInfoModel
import com.polls_example.feature.profile.domain.models.GroupsModel
import com.polls_example.legacy.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDateTime

class GroupingRepository {

    suspend fun getGroups(userId: Int): GroupsModel = suspendTransaction {
        val groupsByUser = GroupsDAO
            .find { GroupsTable.userId eq userId }
            .toList()

        val groupsModel = groupsByUser.map { group ->
            getGroupInfoModel(group)
        }

        return@suspendTransaction GroupsModel(groupsModel)
    }

    suspend fun createGroup(userId: Int, name: String, membersId: List<Int>): GroupInfoModel = suspendTransaction {
        val groupDao = GroupsDAO.new {
            groupName = name
            createdAt = LocalDateTime.now()
            this.userId = userId
            updatedAt = null
        }

        membersId.forEach { memberId ->
            GroupItemsDAO.new {
                groupId = groupDao.id.value
                createdAt = LocalDateTime.now()
                this.memberId = memberId
            }
        }

        return@suspendTransaction getGroupInfoModel(groupDao)
    }

    suspend fun deleteGroup(userId: Int, groupId: Int) = suspendTransaction {
        val group = groupsDAO(groupId, userId)

        GroupsTable.deleteWhere {
            GroupsTable.id eq group.id.value
        }
    }

    suspend fun getGroup(userId: Int, groupId: Int): GroupInfoModel = suspendTransaction {
        val group = groupsDAO(groupId, userId)
        return@suspendTransaction getGroupInfoModel(group)
    }

    suspend fun addMember(userId: Int, groupId: Int, memberId: Int) = suspendTransaction {
        val group = groupsDAO(groupId, userId)

        val groupItem = GroupItemsDAO
            .find { (GroupItemsTable.groupId eq group.id.value) and (GroupItemsTable.memberId eq memberId) }
            .singleOrNull()

        if (groupItem != null) throw GroupSampleException("Пользователь уже состоит в группе")
        GroupItemsDAO.new {
            this.groupId = groupId
            this.memberId = memberId
            this.createdAt = LocalDateTime.now()
        }

        val newGroups = GroupItemsDAO
            .find { GroupItemsTable.groupId eq groupId }
    }

    private fun groupsDAO(groupId: Int, userId: Int): GroupsDAO {
        val group = GroupsDAO.findById(groupId) ?: throw GroupException404()
        if (group.userId != userId) throw GroupException403()
        return group
    }

    private fun getGroupInfoModel(group: GroupsDAO): GroupInfoModel {
        val membersInGroup = GroupItemsDAO
            .find { GroupItemsTable.groupId eq group.id.value }
            .toList()

        return groupInfoModel(group.id.value, group.groupName, membersInGroup)
    }

    private fun groupInfoModel(
        groupId: Int,
        groupName: String,
        membersInGroup: List<GroupItemsDAO>
    ) = GroupInfoModel(
        id = groupId,
        name = groupName,
        membersId = membersInGroup.map { it.memberId }
    )
}
