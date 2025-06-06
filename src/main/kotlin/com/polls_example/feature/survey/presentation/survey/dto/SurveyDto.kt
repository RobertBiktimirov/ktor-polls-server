package com.polls_example.feature.survey.presentation.survey.dto

import com.polls_example.feature.survey.domain.models.QuestionTypeModel
import kotlinx.serialization.Serializable

@Serializable
data class SurveyRequestDto(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val image: String? = null,
    val questions: List<QuestionRequestDto>
)

@Serializable
data class QuestionRequestDto(
    val id: Int? = null,
    val title: String,
    val image: String? = null,
    val type: QuestionTypeModel,
    val answers: List<AnswerOptionRequestDto>? = null
)

@Serializable
data class AnswerOptionRequestDto(
    val id: Int? = null,
    val text: String,
)

@Serializable
data class SurveyInvitationsRequestDto(
    val surveyId: Int,
    val listUserId: List<Int> = emptyList()
)

@Serializable
data class SurveyInvitationDeleteRequestDto(
    val surveyId: Int,
    val userId: Int,
)

@Serializable
data class SurveyUpdateInfoRequestDto(
    val id: Int,
    val title: String,
    val image: String?,
    val description: String?
)

@Serializable
data class PassSurveyDto(
    val surveyId: Int,
    val passQuestions: List<PassQuestionDto>
)

@Serializable
data class PassQuestionDto(
    val questionId: Int,
    val responseText: String?,
    val passAnswersOptionsId: List<Int>
)