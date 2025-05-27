package com.polls_example.feature.files.presentation

import com.polls_example.feature.files.presentation.dto.UserAvatarDto
import com.polls_example.ioc.AppComponent
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import java.io.File
import java.util.*

fun Application.setupFileUploadRouting(component: AppComponent) {
    routing {
        getImageRoute()
        postImageRoute()
    }
}

private fun Routing.postImageRoute() {
    post("/upload/image") {
        var part: PartData? = null
        try {
            val multiPart = call.receiveMultipart()
            var fileUrl: String? = null

            multiPart.forEachPart { currentPart ->
                part = currentPart
                if (currentPart is PartData.FileItem) {
                    val originalFileName =
                        currentPart.originalFileName ?: throw IllegalArgumentException("File name is missing")

                    // Проверяем расширение файла
                    if (!originalFileName.endsWith(".jpg", true) &&
                        !originalFileName.endsWith(".jpeg", true) &&
                        !originalFileName.endsWith(".png", true)
                    ) {
                        throw IllegalArgumentException("Only JPG/JPEG/PNG images are allowed")
                    }

                    // Генерируем уникальное имя файла
                    val fileExtension = originalFileName.substringAfterLast('.', "")
                    val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
                    val file = File("${Paths.RESOURCE_PATH}/$uniqueFileName").absoluteFile

                    // Создаем директорию, если ее нет
                    file.parentFile.mkdirs()

                    try {
                        withContext(Dispatchers.IO) {
                            file.outputStream().use { outputStream ->
                                @Suppress("DEPRECATION") val channel = currentPart.streamProvider()
                                channel.copyTo(outputStream)
                            }
                        }

                        fileUrl = "/images/$uniqueFileName"
                    } catch (e: IOException) {
                        file.delete() // Удаляем частично записанный файл при ошибке
                        throw e
                    }
                }
                currentPart.dispose()
                part = null
            }

            fileUrl?.let { url ->
                call.respond(HttpStatusCode.OK, UserAvatarDto(url))
            } ?: call.respond(HttpStatusCode.BadRequest, "No image file found in request")
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "File upload failed: ${e.message}"))
        } finally {
            part?.dispose?.let { it() }
        }
    }
}

private fun Routing.getImageRoute() {
    get("/images/{filename}") {
        val filename = call.parameters["filename"] ?: throw IllegalArgumentException("Filename parameter is missing")

        // 1. Упростим проверку имени файла
        if (!filename.matches(Regex("^[a-zA-Z0-9-_.]+\\.(jpg|jpeg|png)$", RegexOption.IGNORE_CASE))) {
            call.respond(HttpStatusCode.BadRequest, "Invalid filename format: $filename")
            return@get
        }

        // 2. Исправляем путь к файлу (убираем лишнее replace)
        val file = File("${Paths.RESOURCE_PATH}/$filename").absoluteFile

        println("Trying to access file at: ${file.absolutePath}") // Логируем путь

        // 3. Добавляем проверку на доступность файла
        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound, "Image not found at: ${file.absolutePath}")
            return@get
        }
        if (!file.isFile) {
            call.respond(HttpStatusCode.Conflict, "Path is not a file")
            return@get
        }

        // 4. Устанавливаем правильный Content-Type
        val contentType = when (file.extension.lowercase()) {
            "jpg", "jpeg" -> ContentType.Image.JPEG
            "png" -> ContentType.Image.PNG
            else -> ContentType.Application.OctetStream
        }

        call.response.header(HttpHeaders.ContentType, contentType.toString())
        call.respondFile(file)
    }
}