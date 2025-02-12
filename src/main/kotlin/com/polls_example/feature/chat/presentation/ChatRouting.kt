package com.polls_example.feature.chat.presentation

import com.polls_example.ioc.AppComponent
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.setupChatRouting(appComponent: AppComponent) {

    routing {
        authenticate("another-auth") {
            webSocket("/chat/{surveyId}/{userId}") {

                val surveyId = call.parameters["surveyId"]?.toIntOrNull()
                val userId = call.parameters["userId"]?.toIntOrNull()

                if (surveyId == null || userId == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid parameters"))
                    return@webSocket
                }

                try {
                    for (message in incoming) {
                        when(message) {
                            is Frame.Binary -> TODO()
                            is Frame.Close -> TODO()
                            is Frame.Ping -> TODO()
                            is Frame.Pong -> TODO()
                            is Frame.Text -> {

                            }
                        }
                    }
                } finally {

                }
            }
        }
    }
}