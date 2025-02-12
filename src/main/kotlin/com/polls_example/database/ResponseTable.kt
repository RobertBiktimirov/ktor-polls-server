package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object ResponseTable : IntIdTable("responses") {

    val surveyResponseId =
        integer("survey_response_id").references(SurveyResponseTable.id, onDelete = ReferenceOption.CASCADE)

    val questionId = integer("question_id").references(QuestionTable.id, onDelete = ReferenceOption.CASCADE)
    val text = text("text")
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
}

class ResponseDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ResponseDAO>(ResponseTable)

    val surveyResponseId by ResponseTable.surveyResponseId
    val questionId by ResponseTable.questionId
    val text by ResponseTable.text
    val createdAt by ResponseTable.createdAt
    val updatedAt by ResponseTable.updatedAt
}