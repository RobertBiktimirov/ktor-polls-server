package com.polls_example.ioc.beans

import com.polls_example.feature.chat.presentation.ChatController
import com.polls_example.feature.login.presentation.LoginController
import com.polls_example.feature.profile.presentation.grouping.GroupingController
import com.polls_example.feature.survey.presentation.question.QuestionController
import com.polls_example.feature.survey.presentation.survey.SurveyController
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

    singleton<QuestionController> {
        QuestionController(
            surveyRepository = get()
        )
    }

    singleton<ChatController> {
        ChatController(
            repository = get()
        )
    }

    singleton<GroupingController> {
        GroupingController(
            repository = get()
        )
    }
}