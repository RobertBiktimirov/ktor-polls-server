package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object ChatTable : IntIdTable("chats") {
    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val surveyId = integer("survey_id").references(SurveyTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").nullable()
}

class ChatDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ChatDAO>(ChatTable)

    var userId by ChatTable.userId
    var surveyId by ChatTable.surveyId
    var createdAt by ChatTable.createdAt
}