package com.polls_example.feature.survey.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurveyResponsesInfoModel(
    val model: SurveyInfoModel?,
    val responses: List<SurveyResponseInfoModel>,
)

@Serializable
data class SurveyInfoModel(
    val id: Int,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val authorEmail: String?,
    val authorId: Int?,
    val updateTime: Long?,
    val createdTime: Long
)

@Serializable
data class SurveyResponseInfoModel(
    val user: SurveyResponseUserModel?,
    val answers: List<SurveyQuestionResponseModel>
)

@Serializable
data class SurveyResponseUserModel(
    val id: Int,
    val email: String,
    val name: String,
    val image: String?,
)

@Serializable
data class SurveyQuestionResponseModel(
    val question: String,
    val answer: String,
)

@Serializable
data class SurveyReceivedInfoModel(
    val info: SurveyInfoModel?,
    val questions: List<SurveyQuestionInfoModel>,
)

@Serializable
data class SurveyQuestionInfoModel(
    val questionId: Int,
    val text: String,
    val imageUrl: String?,
    val type: QuestionTypeModel,
    val createdTime: Long,
    val updateTime: Long?,
    val isActive: Boolean,
    val answers: List<AnswerOptionModel>?
)

@Serializable
data class AnswerOptionModel(
    val id: Int = 0,
    val text: String,
)

@Serializable
enum class QuestionTypeModel {

    @SerialName("text")
    TEXT,

    @SerialName("answers")
    ANSWERS,

}