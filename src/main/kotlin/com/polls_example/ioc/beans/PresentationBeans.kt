package com.polls_example.ioc.beans

import com.polls_example.feature.login.presentation.LoginController
import com.polls_example.feature.survey.presentation.SurveyController
import scout.definition.Registry

fun Registry.usePresentationBeans() {
    singleton<LoginController> {
        LoginController(
            userRepository = get(),
            jwtService = get(),
            emailService = get(),
            cacheProvider = get()
        )
    }

    singleton<SurveyController> {
        SurveyController(
            surveyRepository = get()
        )
    }
}