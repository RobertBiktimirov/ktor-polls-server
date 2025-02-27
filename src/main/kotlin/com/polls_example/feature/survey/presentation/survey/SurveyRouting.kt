package com.polls_example.feature.survey.presentation.survey

import com.polls_example.feature.survey.presentation.question.setupQuestionRouting
import com.polls_example.feature.survey.presentation.survey.dto.SurveyInvitationDeleteRequestDto
import com.polls_example.feature.survey.presentation.survey.dto.SurveyInvitationsRequestDto
import com.polls_example.feature.survey.presentation.survey.dto.SurveyRequestDto
import com.polls_example.feature.survey.presentation.survey.dto.SurveyUpdateInfoRequestDto
import com.polls_example.ioc.AppComponent
import com.polls_example.security.CLAIM_USER_ID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun ApplicationCall.getUserId(): Int? =
    principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(CLAIM_USER_ID)
        ?.asInt()

fun Application.setupSurveyRouting(appComponent: AppComponent) {
    setupQuestionRouting(appComponent.getQuestionController())

    val controller = appComponent.getSurveyController()

    routing {
        authenticate("another-auth") {
            get("/surveys/received") {
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val response = controller.getSurveysInvitation(userId)
                call.respond(HttpStatusCode.OK, response)
            }

            get("surveys/created") {
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val response = controller.getSurveysUser(userId)
                call.respond(HttpStatusCode.OK, response)
            }

            get("/surveys/received/{id}") {
                val surveyId = call.pathParameters["id"]?.toIntOrNull()
                    ?: return@get call.response.status(HttpStatusCode.BadRequest)

                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val response = controller.getSurveyReceivedInfo(userId, surveyId)
                call.respond(HttpStatusCode.OK, response)
            }

            get("surveys/created/{id}/responses/") {
                val surveyId = call.pathParameters["id"]?.toIntOrNull()
                    ?: return@get call.response.status(HttpStatusCode.BadRequest)
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)

                val response = controller.getSurveyUserResponsesInfo(userId, surveyId)
                call.respond(HttpStatusCode.OK, response)
            }

            get("surveys/search") {
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val query = call.queryParameters["query"] ?: ""
                val response = controller.getSurveysByQuery(userId, query)
                call.respond(HttpStatusCode.OK, response)
            }

            post("surveys/add") {
                val userId = call.getUserId() ?: return@post call.response.status(HttpStatusCode.Unauthorized)
                val requestDto = call.receive<SurveyRequestDto>()
                val newSurvey = controller.saveSurvey(userId, requestDto)
                call.respond(HttpStatusCode.OK, newSurvey)
            }

            post("surveys/invitation") {
                val requestDto = call.receive<SurveyInvitationsRequestDto>()
                controller.inviteUsersInSurvey(requestDto)
                call.respond(HttpStatusCode.OK)
            }

            delete("surveys/invitation") {
                val requestDto = call.receive<SurveyInvitationDeleteRequestDto>()
                controller.deleteInviteUserInSurvey(requestDto)
                call.respond(HttpStatusCode.OK)
            }

            put("surveys/updateInfo") {
                val requestDto = call.receive<SurveyUpdateInfoRequestDto>()
                val response = controller.updateSurveyInfo(requestDto)
                call.respond(HttpStatusCode.OK, response)
            }

            delete("surveys/delete/{id}") {
                val id = call
                    .pathParameters["id"]
                    ?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                controller.deleteSurvey(id)
                call.respond(HttpStatusCode.OK)
            }

            put("surveys/block/{id}") {
                val id = call.pathParameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                controller.blockSurvey(id)
                call.respond(HttpStatusCode.OK)
            }

            put("surveys/activate/{id}") {
                val id = call.pathParameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                controller.activateSurvey(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}