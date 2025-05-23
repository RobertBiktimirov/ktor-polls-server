package com.polls_example.feature.chat.presentation

import com.polls_example.feature.survey.presentation.survey.getUserId
import com.polls_example.ioc.AppComponent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.setupChatRouting(appComponent: AppComponent) {

    val server = appComponent.getTwoPersonChatServer()
    val controller = appComponent.getChatController()

    routing {
        authenticate("another-auth") {
            get("/chats") {
                val userId = call.getUserId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val chats = controller.getChats(userId)
                call.respond(HttpStatusCode.OK, chats)
            }

            get("/chat/history") {
                val surveyId = call.request.queryParameters["surveyId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid surveyId")

                val userIdFrom = call.request.queryParameters["id-from"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id-from")

                val userIdTo = call.request.queryParameters["id-to"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id-to")

                val page = call.request.queryParameters["page"]?.toIntOrNull()?.takeIf { it > 0 }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid page number")

                val model = controller.getChatMessageModel(
                    surveyId = surveyId,
                    idTo = userIdTo,
                    idFrom = userIdFrom,
                    page = page
                )

                call.respond(HttpStatusCode.OK, model)
            }
        }

        webSocket("/chat/read-messages") {
            try {
                for (frame in incoming) {
                    when(frame) {
                        is Frame.Text -> { controller.setReadMessage(frame.readText()) }
                        else -> {}
                    }
                }
            } catch (e: Exception) {}
        }

        webSocket("/chat/{surveyId}/{id-from}/{id-to}") {

            val surveyId = call.parameters["surveyId"]?.toIntOrNull()
            val emailFrom = call.parameters["id-from"]
            val emailTo = call.parameters["id-to"]

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
                            controller.sendMessage(frame.readText())
                                .onSuccess {
                                    println("success send message $it")
                                    server.sendMessage(emailTo, emailFrom, surveyId, it)
                                }
                                .onFailure {
                                    println("failure send message $it")
                                }
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