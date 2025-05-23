package com.polls_example.ioc.beans

import com.polls_example.feature.chat.presentation.ChatController
import com.polls_example.feature.files.presentation.FileController
import com.polls_example.feature.login.presentation.LoginController
import com.polls_example.feature.login.presentation.PasswordValidator
import com.polls_example.feature.profile.presentation.grouping.GroupingController
import com.polls_example.feature.survey.presentation.question.QuestionController
import com.polls_example.feature.survey.presentation.survey.SurveyController
import com.polls_example.legacy.verificationPassword
import scout.definition.Registry

fun Registry.usePresentationBeans() {
    singleton<LoginController> {
        LoginController(
            userRepository = get(),
            jwtService = get(),
            emailService = get(),
            cacheProvider = get(),
            passwordValidator = get()
        )
    }

    singleton<SurveyController> {
        SurveyController(
            surveyRepository = get(),
            userRepository = get()
        )
    }

    singleton<QuestionController> {
        QuestionController(
            surveyRepository = get()
        )
    }

    singleton<ChatController> {
        ChatController(
            repository = get(),
            userRepository = get(),
            surveyRepository = get()
        )
    }

    singleton<GroupingController> {
        GroupingController(
            repository = get(),
            userRepository = get()
        )
    }

    singleton<PasswordValidator> {
        object : PasswordValidator {
            override fun validatePassword(password: String, hashPassword: String): Boolean {
                return verificationPassword(password, hashPassword)
            }
        }
    }

    singleton<FileController> {
        FileController(
            userRepository = get(),
        )
    }
}