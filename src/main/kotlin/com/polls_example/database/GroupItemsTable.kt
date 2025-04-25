package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object GroupItemsTable : IntIdTable("group_items") {
    val groupId = integer("group_id").references(GroupsTable.id, onDelete = ReferenceOption.CASCADE)
    val memberId = integer("member_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").nullable()
}

class GroupItemsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupItemsDAO>(GroupItemsTable)
    var groupId by GroupItemsTable.groupId
    var createdAt by GroupItemsTable.createdAt
    var memberId by GroupItemsTable.memberId
}