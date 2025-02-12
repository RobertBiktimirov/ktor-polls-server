package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object QuestionTable : IntIdTable("questions") {
    val surveyId = integer("survey_id").references(SurveyTable.id, onDelete = ReferenceOption.CASCADE)
    val text = text("text")
    val imageUrl = varchar("image_url", 255).nullable()
    /**
     * 0 - text
     *
     * 1 - answers
     */
    val type = integer("type")
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable().default(null)
    val isActive = bool("is_active").default(true)
}

class QuestionDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuestionDAO>(QuestionTable)

    var surveyId by QuestionTable.surveyId
    var text by QuestionTable.text
    var imageUrl by QuestionTable.imageUrl

    /**
     * 0 - text
     *
     * 1 - answers
     */
    var type by QuestionTable.type
    var createdAt by QuestionTable.createdAt
    var updatedAt by QuestionTable.updatedAt
    var isActive by QuestionTable.isActive
}