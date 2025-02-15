package com.polls_example.feature.survey.domain.models

import com.polls_example.feature.survey.presentation.survey.dto.QuestionRequestDto

sealed class SurveyUpdate(
    open val title: String,
    open val description: String?,
    open val image: String?,
) {

    data class SurveySaveModel(
        override val title: String,
        override val description: String?,
        override val image: String?,
        val questions: List<QuestionRequestDto>,
    ) : SurveyUpdate(title, description, image)


    data class SurveyUpdateModel(
        override val title: String,
        override val description: String?,
        override val image: String?,
    ) : SurveyUpdate(title, description, image)
}