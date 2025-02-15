package com.polls_example.feature.survey.presentation.question

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.setupQuestionRouting(controller: QuestionController) {

    routing {
        authenticate("another-auth") {

        }
    }

}