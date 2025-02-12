package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object AnswerOptionTable: IntIdTable("answer_options") {
    val questionId = integer("question_id").references(QuestionTable.id, onDelete = ReferenceOption.CASCADE)
    val text = text("text")
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
}

class AnswerOptionDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<AnswerOptionDAO>(AnswerOptionTable)
    var questionId by AnswerOptionTable.questionId
    var text by AnswerOptionTable.text
    var createdAt by AnswerOptionTable.createdAt
    var updatedAt by AnswerOptionTable.updatedAt
}