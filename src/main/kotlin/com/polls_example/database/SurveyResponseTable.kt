package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object SurveyResponseTable : IntIdTable("survey_responses") {

    val surveyId = integer("survey_id").references(SurveyTable.id, onDelete = ReferenceOption.CASCADE)
    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val completedAt = datetime("completed_at").nullable()
}

class SurveyResponsesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SurveyResponsesDAO>(SurveyResponseTable)

    var surveyId by SurveyResponseTable.surveyId
    var userId by SurveyResponseTable.userId
    var completedAt by SurveyResponseTable.completedAt
}