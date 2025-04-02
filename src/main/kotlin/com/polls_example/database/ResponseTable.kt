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
    val text = text("text").nullable()
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
}

class ResponseDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ResponseDAO>(ResponseTable)

    var surveyResponseId by ResponseTable.surveyResponseId
    var questionId by ResponseTable.questionId
    var text by ResponseTable.text
    var createdAt by ResponseTable.createdAt
    var updatedAt by ResponseTable.updatedAt
}