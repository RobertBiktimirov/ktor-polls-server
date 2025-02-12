package com.polls_example.feature.survey.data.repository

import com.polls_example.database.*
import com.polls_example.feature.survey.domain.exception.SurveyDeleteInvitationException
import com.polls_example.feature.survey.domain.exception.SurveyExceptions
import com.polls_example.feature.survey.domain.exception.SurveyForbiddenException
import com.polls_example.feature.survey.domain.models.*
import com.polls_example.legacy.suspendTransaction
import com.polls_example.legacy.timeInMillis
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import java.time.LocalDateTime

class SurveyRepository {

    suspend fun getSurveysInvitation(userId: Int): List<SurveysModel> = suspendTransaction {

        val invitationSurveys = SurveyInvitationsDAO
            .find { SurveyInvitationsTable.userId eq userId }
            .toList()

        val surveysList = SurveyDAO
            .find { SurveyTable.id inList invitationSurveys.map { it.surveyId } }
            .toList()

        val modelList = surveysList.map { dao ->
            SurveysModel(
                id = dao.id.value,
                title = dao.title,
                isCompleted = invitationSurveys.firstOrNull { it.surveyId == it.id.value }?.completedAt != null,
                image = dao.imageUrl,
                isActive = dao.isActive
            )
        }

        return@suspendTransaction modelList
    }

    suspend fun getSurveysUser(userId: Int): List<SurveysModel> = suspendTransaction {

        val surveysList = SurveyDAO
            .find { SurveyTable.userId eq userId }
            .toList()

        val modelList = surveysList.map { dao ->
            SurveysModel(
                id = dao.id.value,
                title = dao.title,
                image = dao.imageUrl,
                isCompleted = true,
                isActive = dao.isActive
            )
        }

        return@suspendTransaction modelList
    }

    suspend fun getSurveyInfo(
        userId: Int,
        surveyId: Int,
        isCreated: Boolean = true
    ): SurveyInfoModel? = suspendTransaction {
        val surveyDAO = SurveyDAO
            .find { SurveyTable.id eq surveyId }
            .singleOrNull()
            ?: return@suspendTransaction null

        if (surveyDAO.userId != userId && isCreated) throw SurveyForbiddenException()

        return@suspendTransaction SurveyInfoModel(
            id = surveyId,
            title = surveyDAO.title,
            description = surveyDAO.description,
            createdTime = surveyDAO.createdAt?.timeInMillis ?: 0L,
            imageUrl = surveyDAO.imageUrl,
            isActive = surveyDAO.isActive,
            updateTime = surveyDAO.updatedAt?.timeInMillis
        )
    }

    suspend fun getSurveysResponses(surveyId: Int): List<SurveyResponseInfoModel> = suspendTransaction {
        val surveyResponses = SurveyResponsesDAO
            .find { SurveyResponseTable.surveyId eq surveyId }
            .toList()

        return@suspendTransaction surveyResponses.map {
            val user = getSurveyResponseUser(it.userId)
            val answers = getSurveyResponseAnswers(it.id.value)
            SurveyResponseInfoModel(user, answers)
        }
    }

    suspend fun getSurveyQuestions(surveyId: Int): List<SurveyQuestionInfoModel> = suspendTransaction {

        val questionsDao = QuestionDAO
            .find { QuestionTable.surveyId eq surveyId }
            .toList()

        return@suspendTransaction questionsDao.map {
            val questionId: Int = it.id.value
            val text: String = it.text
            val imageUrl: String? = it.imageUrl
            val type: QuestionTypeModel = when (it.type) {
                1 -> QuestionTypeModel.ANSWERS
                else -> QuestionTypeModel.TEXT
            }
            val createdTime: Long = it.createdAt?.timeInMillis ?: 0L
            val updateTime: Long? = it.updatedAt?.timeInMillis
            val isActive: Boolean = it.isActive
            val answers: List<AnswerOptionModel>? = if (type == QuestionTypeModel.TEXT)
                null
            else
                getQuestionAnswers(questionId)

            SurveyQuestionInfoModel(
                questionId = questionId,
                text = text,
                imageUrl = imageUrl,
                type = type,
                createdTime = createdTime,
                updateTime = updateTime,
                isActive = isActive,
                answers = answers,
            )
        }
    }

    suspend fun getUserAllSurveysByQuery(userId: Int, query: String): List<SurveysModel> = suspendTransaction {

        // Получаем все приглашения на опросы для данного пользователя
        val invitationSurveys = SurveyInvitationsDAO
            .find { SurveyInvitationsTable.userId eq userId }
            .toList()

        // Получаем все опросы, созданные пользователем или на которые у него есть приглашения
        val surveyIds = invitationSurveys.map { it.surveyId }
        val surveys = SurveyDAO
            .find { (SurveyTable.userId eq userId) or (SurveyTable.id inList surveyIds) }
            // Фильтруем по заголовку
            .filter { it.title.contains(query, ignoreCase = true) }
            .map { survey ->
                var isCompleted: Boolean? = invitationSurveys
                    .firstOrNull { invitation -> invitation.surveyId == survey.id.value }
                    ?.completedAt != null

                val isUserCreated = survey.userId == userId
                if (isUserCreated) isCompleted = null
                SurveysModel(
                    id = survey.id.value,
                    title = survey.title,
                    image = survey.imageUrl,
                    isCompleted = if (isUserCreated) null else isCompleted,
                    isUserCreated = isUserCreated,
                    isActive = survey.isActive
                )
            }

        surveys // Возвращаем отфильтрованный список
    }

    suspend fun updateSurvey(surveyId: Int, survey: SurveyUpdate.SurveyUpdateModel): SurveyInfoModel =
        suspendTransaction {
            val surveyDao = SurveyDAO.findById(surveyId) ?: throw SurveyExceptions("Опрос не найден")
            surveyDao.title = survey.title
            surveyDao.description = survey.description
            surveyDao.imageUrl = survey.image
            surveyDao.updatedAt = LocalDateTime.now()
            surveyDao.flush()

            return@suspendTransaction SurveyInfoModel(
                id = surveyDao.id.value,
                title = surveyDao.title,
                description = surveyDao.description,
                imageUrl = surveyDao.imageUrl,
                isActive = surveyDao.isActive,
                updateTime = surveyDao.updatedAt?.timeInMillis,
                createdTime = surveyDao.createdAt?.timeInMillis ?: 0L
            )
        }

    suspend fun saveSurvey(userId: Int, survey: SurveyUpdate.SurveySaveModel): SurveyInfoModel = suspendTransaction {
        val surveyDao = SurveyDAO.new {
            this.userId = userId
            title = survey.title
            description = survey.description
            imageUrl = survey.image
            createdAt = LocalDateTime.now()
        }

        survey.questions.forEach { questionRequestDto ->
            val question = QuestionDAO.new {
                surveyId = surveyDao.id.value
                text = questionRequestDto.title
                imageUrl = questionRequestDto.image
                type = questionRequestDto.type.ordinal
                createdAt = surveyDao.createdAt
            }

            questionRequestDto.answers?.forEach { answerRequestDto ->
                AnswerOptionDAO.new {
                    questionId = question.id.value
                    text = answerRequestDto.text
                    createdAt = question.createdAt
                }
            }
        }

        return@suspendTransaction SurveyInfoModel(
            id = surveyDao.id.value,
            title = surveyDao.title,
            description = surveyDao.description,
            imageUrl = surveyDao.imageUrl,
            isActive = surveyDao.isActive,
            updateTime = surveyDao.updatedAt?.timeInMillis,
            createdTime = surveyDao.createdAt?.timeInMillis ?: 0L
        )
    }

    suspend fun inviteUsersToSurvey(vararg usersId: Int, surveyId: Int) = suspendTransaction {

        for (usId in usersId) {
            val invitationsDAO = SurveyInvitationsDAO
                .find { (SurveyInvitationsTable.userId eq usId) and (SurveyInvitationsTable.surveyId eq surveyId) }
                .singleOrNull()

            if (invitationsDAO != null) continue

            SurveyInvitationsDAO.new {
                this.surveyId = surveyId
                userId = usId
                invitedAt = LocalDateTime.now()
            }
        }
    }

    suspend fun deleteSurvey(surveyId: Int) = suspendTransaction {
        SurveyTable.deleteWhere {
            SurveyTable.id eq surveyId
        }
    }

    suspend fun deleteInviteUserToSurvey(userId: Int, surveyId: Int) = suspendTransaction {

        val invitationsDAO = SurveyInvitationsDAO
            .find { (SurveyInvitationsTable.userId eq userId) and (SurveyInvitationsTable.surveyId eq surveyId) }
            .singleOrNull()

        if (invitationsDAO?.completedAt != null) throw SurveyDeleteInvitationException()

        SurveyInvitationsTable
            .deleteWhere {
                SurveyInvitationsTable.id eq invitationsDAO?.id?.value
            }
    }

    suspend fun activateSurvey(surveyId: Int) = suspendTransaction {
        changeSurveyActiveState(surveyId, true)
    }

    suspend fun blockSurvey(surveyId: Int) = suspendTransaction {
        changeSurveyActiveState(surveyId, false)
    }

    private fun changeSurveyActiveState(surveyId: Int, isActive: Boolean): Boolean {
        val survey = SurveyDAO.findById(surveyId) ?: throw SurveyExceptions("Не удалось найти опрос")
        survey.isActive = isActive
        return survey.flush()
    }

    private fun getQuestionAnswers(questionId: Int): List<AnswerOptionModel> =
        AnswerOptionDAO
            .find { AnswerOptionTable.questionId eq questionId }
            .toList()
            .map {
                AnswerOptionModel(
                    id = it.id.value,
                    text = it.text
                )
            }

    private fun getSurveyResponseUser(userId: Int): SurveyResponseUserModel? {
        val user = UserDAO.findById(userId) ?: return null
        return SurveyResponseUserModel(
            email = user.email,
            name = user.name,
            image = user.image,
            lastActiveTime = user.lastActivity?.timeInMillis
        )
    }

    private fun getSurveyResponseAnswers(surveyResponseId: Int): List<SurveyQuestionResponseModel> {
        val responses = ResponseDAO
            .find { ResponseTable.surveyResponseId eq surveyResponseId }
            .toList()

        return responses.map {
            val question = QuestionDAO.findById(it.questionId)?.text ?: "Не удалось найти вопрос"
            var answer = it.text

            if (answer.isEmpty()) {
                val answerOption = AnswerOptionDAO
                    .find { AnswerOptionTable.questionId eq it.questionId }
                    .singleOrNull()
                    ?.text

                answer = answerOption ?: "Не удалось найти ответ"
            }

            SurveyQuestionResponseModel(question, answer)
        }
    }
}