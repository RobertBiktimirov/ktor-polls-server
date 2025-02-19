package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object GroupsTable : IntIdTable("groups") {
    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable().default(null)
}

class GroupsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupsDAO>(GroupsTable)
    var userId by GroupsTable.userId
    var createdAt by GroupsTable.createdAt
    var updatedAt by GroupsTable.updatedAt
}