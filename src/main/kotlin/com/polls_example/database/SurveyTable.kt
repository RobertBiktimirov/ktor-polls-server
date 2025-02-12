package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object SurveyTable : IntIdTable("surveys") {

    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val imageUrl = varchar("image_url", 255).nullable()
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
    val isActive = bool("is_active").default(true)
}

class SurveyDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<SurveyDAO>(SurveyTable)

    var userId by SurveyTable.userId
    var title by SurveyTable.title
    var description by SurveyTable.description
    var imageUrl by SurveyTable.imageUrl
    var createdAt by SurveyTable.createdAt
    var updatedAt by SurveyTable.updatedAt
    var isActive by SurveyTable.isActive
}