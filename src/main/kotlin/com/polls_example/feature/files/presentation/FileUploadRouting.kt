package com.polls_example.feature.files.presentation

import com.polls_example.feature.files.presentation.dto.UserAvatarDto
import com.polls_example.ioc.AppComponent
import com.polls_example.security.CLAIM_NAME
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private fun ApplicationCall.getUserName(): String? =
    principal<JWTPrincipal>()
        ?.payload
        ?.getClaim(CLAIM_NAME)
        ?.asString()

fun Application.setupFileUploadRouting(component: AppComponent) {

    routing {
        getImageRoute()
        post("/upload/avatar") {
            val multiPart = call.receiveMultipart()
            multiPart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName
                    val file = File("${Paths.RESOURCE_AVATARS_PATH}/$fileName")

//                    file.parentFile.mkdirs()

                    withContext(Dispatchers.IO) {
                        file.outputStream().use { outputStream ->
                            @Suppress("DEPRECATION")
                            part.streamProvider().copyTo(outputStream)
                        }
                    }

                    call.respond(HttpStatusCode.OK, UserAvatarDto("avatar/$fileName"))
                }
                part.dispose()
            }
        }
    }
}

fun Routing.getImageRoute() {
    authenticate("another-auth") {
        get("/avatar/{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing or malformed file name"
            )

            val file = File("${Paths.RESOURCE_AVATARS_PATH}/$fileName")
            if (file.exists()) {
                call.respondFile(file, fileName)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }
    }
}