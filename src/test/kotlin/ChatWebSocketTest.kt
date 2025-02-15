package com.polls_example

import com.polls_example.feature.chat.presentation.setupChatRouting
import com.polls_example.ioc.AppComponent
import io.ktor.client.plugins.websocket.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ChatWebSocketTest {

    @Test
    fun testWebSocketConnection() = testApplication {
        setupChatWebSocket()

        val client = createClient { install(WebSockets) }

        client.webSocket("/chat/1/user_email@mail.ru/user2@email.com") {
            close()
        }
    }

    @Test
    fun testSendMessage() = testApplication {
        setupChatWebSocket()

        val surveyId = 1
        val email1 = "user1@example.com"
        val email2 = "user2@example.com"

        val receivedMessages = Collections.synchronizedList(mutableListOf<String>())

        val scope = CoroutineScope(Dispatchers.Default)

        // Запускаем оба подключения параллельно
        val client1Job = scope.async {
            val client = createClient { install(WebSockets) }
            client.webSocket("/chat/$surveyId/$email1/$email2") {
                send(Frame.Text("Hello from User1"))
            }
        }

        val client2Job = scope.async {
            val client = createClient { install(WebSockets) }
            client.webSocket("/chat/$surveyId/$email2/$email1") {
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                receivedMessages.add(text)
                            }
                            else -> fail("Unexpected frame type")
                        }
                    }
                } catch (e: Exception) {
                    // Обработка ошибок
                }
            }
        }

        // Даем время на установку соединений
        delay(1000)

        client2Job.start()
        // Отправка сообщения после установки соединений
        client1Job.job.start()

        // Ждем получения сообщения (максимум 3 секунды)
        withTimeoutOrNull(3000) {
            while (receivedMessages.isEmpty()) {
                delay(100)
            }
        }

        println(receivedMessages)
        // Проверяем результаты
        assertEquals("Hello from User1", receivedMessages[0])

        // Отменяем задачи
        client1Job.cancel()
        client2Job.cancel()
    }

    private fun ApplicationTestBuilder.setupChatWebSocket() {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test_secret",
                "jwt.issuer" to "test_issuer",
                "jwt.audience" to "test_audience",
                "jwt.realm" to "test_realm",
                "postgres.url" to "test_db_url",
                "postgres.user" to "test_user",
                "postgres.password" to "test_password",
                "email.address" to "test_email@example.com",
                "email.password" to "test_password",
                "email.from" to "test_from@example.com",
                "email.host_name" to "test_host",
                "email.port" to "587"
            )
        }
        application {
            val appConfig = environment.config.toAppConfig()
            val appComponent = AppComponent(appConfig)
            // Здесь вам нужно будет установить ваше приложение с маршрутизацией
            setupPlugins()
            configureSecurity(appComponent.getJwtService())
            setupChatRouting(appComponent = appComponent) // Подключите вашу маршрутизацию
        }
    }

}