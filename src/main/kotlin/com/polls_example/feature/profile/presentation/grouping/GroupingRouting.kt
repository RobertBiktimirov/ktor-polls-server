package com.polls_example.feature.profile.presentation.grouping

import com.polls_example.feature.profile.presentation.grouping.dto.CreateGroupDto
import com.polls_example.feature.profile.presentation.grouping.dto.EditProfileModel
import com.polls_example.feature.profile.presentation.grouping.dto.MemberGroupDto
import com.polls_example.feature.survey.presentation.survey.getUserId
import com.polls_example.ioc.AppComponent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private fun RoutingContext.getGroupId() = call
    .pathParameters["id"]
    ?.toIntOrNull()

fun Application.setupGroupingRouting(appComponent: AppComponent) {
    val controller = appComponent.getGroupingController()

    routing {
        authenticate("another-auth") {

            // TODO("позже вынести в другой маршрутизатор")
            get("profile") {
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val query = call.queryParameters["email"] ?: ""
                val users = controller.getUsersByEmail(userId = userId, email = query)
                call.respond(HttpStatusCode.OK, users)
            }

            // TODO("позже вынести в другой маршрутизатор")
            get("profile/{id}") {
                val id = call.pathParameters["id"]?.toIntOrNull()
                    ?: return@get call.response.status(HttpStatusCode.BadRequest)
                val user = controller.getUserById(id)
                call.respond(HttpStatusCode.OK, user)
            }

            put("profile") {
                val id = call.getUserId()
                    ?: return@put call.response.status(HttpStatusCode.BadRequest)
                val body = call.receive<EditProfileModel>()
                controller.editUserInfo(userId = id, info = body)
                call.respond(HttpStatusCode.OK)
            }

            get("profile/groups") {
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val response = controller.getGroupsByUserId(userId)
                call.respond(HttpStatusCode.OK, response)
            }

            get("profile/group/{id}") {
                val userId = call.getUserId() ?: return@get call.response.status(HttpStatusCode.Unauthorized)
                val groupId = getGroupId() ?: return@get call.respond(HttpStatusCode.BadRequest)

                val response = controller.getGroupById(userId, groupId)
                call.respond(HttpStatusCode.OK, response)
            }

            put("profile/group/{id}/add-member") {
                val userId = call.getUserId() ?: return@put call.response.status(HttpStatusCode.Unauthorized)
                val groupId = getGroupId() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val requestDto = call.receive<MemberGroupDto>()
                controller.addMemberInGroup(userId, groupId, requestDto.memberId)
                call.respond(HttpStatusCode.OK)
            }

            put("profile/group/{id}/delete-member") {
                val userId = call.getUserId() ?: return@put call.response.status(HttpStatusCode.Unauthorized)
                val groupId = getGroupId() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val requestDto = call.receive<MemberGroupDto>()
                controller.deleteMemberInGroup(userId, groupId, requestDto.memberId)
                call.respond(HttpStatusCode.OK)
            }

            post("profile/group/create") {
                val userId = call.getUserId() ?: return@post call.response.status(HttpStatusCode.Unauthorized)
                val requestDto = call.receive<CreateGroupDto>()
                val response = controller.createGroup(userId, requestDto)
                call.respond(HttpStatusCode.OK, response)
            }

            delete("profile/group/{id}") {
                val userId = call.getUserId() ?: return@delete call.response.status(HttpStatusCode.Unauthorized)
                val groupId = getGroupId() ?: return@delete call.respond(HttpStatusCode.BadRequest)

                controller.deleteGroup(userId, groupId)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}