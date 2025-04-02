package com.polls_example

import com.polls_example.feature.chat.presentation.setupChatRouting
import com.polls_example.feature.files.presentation.setupFileUploadRouting
import com.polls_example.feature.login.domain.exception.UserException
import com.polls_example.feature.login.presentation.setupLoginRouting
import com.polls_example.feature.profile.domain.exceptions.GroupException403
import com.polls_example.feature.profile.domain.exceptions.GroupException404
import com.polls_example.feature.profile.presentation.grouping.setupGroupingRouting
import com.polls_example.feature.survey.domain.exception.SurveyDeleteInvitationException
import com.polls_example.feature.survey.domain.exception.SurveyExceptions
import com.polls_example.feature.survey.domain.exception.SurveyForbiddenException
import com.polls_example.feature.survey.presentation.survey.setupSurveyRouting
import com.polls_example.ioc.AppComponent
import com.polls_example.security.JwtService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.util.converters.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val appConfig = environment.config.toAppConfig()
    val appComponent = AppComponent(appConfig)

    configureSecurity(appComponent.getJwtService())
    setupPlugins()
    launch { setupDatabase(appConfig.databaseConfig) }
    setupRouting(appComponent)

}

fun Application.setupPlugins() {
    install(ContentNegotiation) {
        json()
    }

    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(StatusPages) {

        exception<SurveyExceptions> { call: ApplicationCall, ex: SurveyExceptions ->
            call.respond(HttpStatusCode.UnprocessableEntity, ex.message)
        }

        exception<SurveyForbiddenException> { call: ApplicationCall, _: SurveyForbiddenException ->
            call.respond(HttpStatusCode.Forbidden)
        }

        exception<SurveyDeleteInvitationException> { call: ApplicationCall, _: SurveyDeleteInvitationException ->
            call.respond(HttpStatusCode.UnprocessableEntity, "К сожалению, данный пользователь уже прошел опрос.")
        }

        exception<UserException> { call: ApplicationCall, cause: UserException ->
            call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
        }

        exception<MissingRequestParameterException> { call, cause ->
            call.respondText("Missing \"${cause.parameterName}\" parameter.", status = HttpStatusCode.BadRequest)
        }

        exception<ParameterConversionException> { call, cause ->
            val source = cause.cause

            val message = if (source != null) {
                cause.message.toString() + " - " + source.message.toString()
            } else {
                cause.message.toString()
            }

            call.respond(HttpStatusCode.BadRequest, message)
        }

        exception<SerializationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }

        exception<DataConversionException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }

        exception<GroupException403> { call, _ ->
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<GroupException404> { call, _ ->
            call.respond(HttpStatusCode.NotFound)
        }
    }
}

fun Application.setupDatabase(databaseConfig: DatabaseConfig) {
    Database.connect(
        url = databaseConfig.url,
        user = databaseConfig.user,
        password = databaseConfig.password,
    )
}

fun Application.setupRouting(component: AppComponent) {
    setupLoginRouting(component)
    setupSurveyRouting(component)
    setupChatRouting(component)
    setupGroupingRouting(component)
    setupFileUploadRouting(component)
}

fun Application.configureSecurity(
    jwtService: JwtService
) {
    authentication {
        jwt {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                jwtService.customValidator(credential)
            }
        }

        jwt("another-auth") {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                jwtService.customValidator(credential)
            }
        }
    }
}