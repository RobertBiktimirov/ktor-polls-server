package com.polls_example.security

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*


fun Route.authorized(
    build: Route.() -> Unit
) {
    install(CheckAuthorizationPlugin)
    build()
}

val CheckAuthorizationPlugin = createRouteScopedPlugin(
    name = "CheckTokenPlugin",
) {
    pluginConfig.apply {
        on(AuthenticationChecked) {
            val email = getEmailFromToken(it)
            val expiresTime = getExpiresAt(it)

            println("email = $email expiresTime = $expiresTime")

            val authorized = expiresTime > 0L && email != null
            if (!authorized) it.response.status(HttpStatusCode.Unauthorized)
        }
    }
}

private fun getEmailFromToken(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(CLAIM_EMAIL)
        ?.asString()

private fun getExpiresAt(call: ApplicationCall) =
    call.principal<JWTPrincipal>()
        ?.expiresAt
        ?.time
        ?.minus(System.currentTimeMillis()) ?: 0L