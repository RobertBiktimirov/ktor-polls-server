package com.polls_example.feature.chat.presentation

import com.polls_example.ioc.AppComponent
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.setupChatRouting(appComponent: AppComponent) {

    val server = appComponent.getTwoPersonChatServer()

    routing {
        webSocket("/chat/{surveyId}/{email-from}/{email-to}") {

            val surveyId = call.parameters["surveyId"]?.toIntOrNull()
            val emailFrom = call.parameters["email-from"]
            val emailTo = call.parameters["email-to"]

            println("call method surveyId = $surveyId email from = $emailFrom email to = $emailTo")

            if (surveyId == null || emailFrom == null || emailTo == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid parameters"))
                return@webSocket
            }

            server.memberJoin(emailFrom, surveyId, this)

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            println("frame is text with text = ${frame.readText()}")
                            server.sendMessage(emailTo, surveyId, frame.readText())
                        }

                        is Frame.Close -> {
                            println("frame is close")
                            server.memberLeft(emailTo, surveyId)
                        }

                        else -> {
                            println("frame is not close and is not text")
                        }
                    }
                }
            } catch (e: Exception) {
                println("Exception e = $e")
            } finally {
                server.memberLeft(emailFrom, surveyId)
                close()
            }
        }
    }
}