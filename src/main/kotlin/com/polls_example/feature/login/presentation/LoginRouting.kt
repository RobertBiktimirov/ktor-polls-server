package com.polls_example.feature.login.presentation

import com.polls_example.feature.files.presentation.Paths
import com.polls_example.feature.login.presentation.dto.*
import com.polls_example.ioc.AppComponent
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.setupLoginRouting(appComponent: AppComponent) {
    val controller = appComponent.getLoginController()

    routing {
        post("login/login") {
            val userLoginDto = call.receive<LoginDto>()
            val authUser = controller.authenticate(userLoginDto)

            authUser?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.response.status(HttpStatusCode.Unauthorized)
        }

        post("login/refresh") {
            val request = call.receive<RefreshTokenDto>()
            val newAccessToken = controller.refreshToken(token = request.token)
            newAccessToken?.let {
                call.respond(RefreshTokenDto(it))
            } ?: call.respond(
                message = HttpStatusCode.Unauthorized
            )
        }

        post("login/registration") {
            val dto = call.receive<RegistrationDto>()
            val response = controller.registerUser(
                RegistrationDto(
                    email = dto.email,
                    name = dto.name,
                    password = dto.password,
                    image = dto.image
                )
            )
            call.respond(HttpStatusCode.OK, response)
        }

        post("login/send-confirm-email-code") {
            val email = call.receive<EmailCodeDto>()
            if (email.email.isNullOrEmpty()) return@post call.response.status(HttpStatusCode.BadRequest)
            controller.sendEmailCode(email.email)
            call.response.status(HttpStatusCode.OK)
        }

        post("login/confirm-email") {
            val confirmEmailDto = call.receive<ConfirmEmailDto>()
            val response = controller.checkEmailConfirm(confirmEmailDto)
            if (response) call.response.status(HttpStatusCode.OK)
            else call.response.status(HttpStatusCode.BadRequest)
        }

        post("login/reset-password") {
            val userLoginDto = call.receive<LoginDto>()
            controller.resetPassword(userLoginDto)
            call.response.status(HttpStatusCode.OK)
        }
    }
}