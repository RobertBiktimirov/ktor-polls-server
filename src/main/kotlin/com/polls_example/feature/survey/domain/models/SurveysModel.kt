package com.polls_example.feature.survey.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SurveysModel(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val isCompleted: Boolean? = null,
    val isUserCreated: Boolean = false,
    val isActive: Boolean = true,
)