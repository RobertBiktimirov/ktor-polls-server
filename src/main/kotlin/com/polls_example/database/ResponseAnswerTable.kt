package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object ResponseAnswerTable : IntIdTable("response_answers") {
    val responseId = integer("response_id").references(ResponseTable.id, onDelete = ReferenceOption.CASCADE)
    val answerOptionId =
        integer("answer_option_id").references(AnswerOptionTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
}

class ResponseAnswerDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ResponseAnswerDAO>(ResponseAnswerTable)

    var responseId by ResponseAnswerTable.responseId
    var answerOptionId by ResponseAnswerTable.answerOptionId
    var createdAt by ResponseAnswerTable.createdAt
    var updatedAt by ResponseAnswerTable.updatedAt
}