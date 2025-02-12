package com.polls_example.feature.survey.presentation.dto

import com.polls_example.feature.survey.domain.models.QuestionTypeModel
import kotlinx.serialization.Serializable

@Serializable
data class SurveyRequestDto(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val image: String?,
    val questions: List<QuestionRequestDto>
)

@Serializable
data class QuestionRequestDto(
    val id: Int? = null,
    val title: String,
    val image: String?,
    val type: QuestionTypeModel,
    val answers: List<AnswerOptionRequestDto>?
)

@Serializable
data class AnswerOptionRequestDto(
    val id: Int? = null,
    val text: String,
)

@Serializable
data class SurveyInvitationsRequestDto(
    val surveyId: Int,
    val listUserId: List<Int>
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