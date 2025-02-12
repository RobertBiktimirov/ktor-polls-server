package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object SurveyInvitationsTable : IntIdTable("survey_invitations") {

    val surveyId = integer("survey_id").references(SurveyTable.id, onDelete = ReferenceOption.CASCADE)
    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val completedAt = datetime("completed_at").nullable().default(null)
    val invitedAt = datetime("invited_at").nullable()
}

class SurveyInvitationsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SurveyInvitationsDAO>(SurveyInvitationsTable)

    var surveyId by SurveyInvitationsTable.surveyId
    var userId by SurveyInvitationsTable.userId
    var completedAt by SurveyInvitationsTable.completedAt
    var invitedAt by SurveyInvitationsTable.invitedAt
}