package chats

import com.polls_example.configureSecurity
import com.polls_example.feature.chat.presentation.setupChatRouting
import com.polls_example.ioc.AppComponent
import com.polls_example.setupPlugins
import com.polls_example.toAppConfig
import io.ktor.client.plugins.websocket.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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

        val scope = CoroutineScope(Dispatchers.IO)

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
        client1Job.start()

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

    @Test
    fun testMessageDelivery() = testApplication {
        setupChatWebSocket()

        val surveyId = 1
        val email1 = "user1@example.com"
        val email2 = "user2@example.com"
        val email3 = "user3@example.com"

        val receivedMessagesClient2 = Collections.synchronizedList(mutableListOf<String>())
        val receivedMessagesClient3 = Collections.synchronizedList(mutableListOf<String>())

        val scope = CoroutineScope(Dispatchers.IO)

        // Подключение клиента 1 и отправка сообщения
        val client1Job = scope.async {
            val client = createClient { install(WebSockets) }
            client.webSocket("/chat/$surveyId/$email1/$email2") {
                send(Frame.Text("Hello User 2!"))
            }
        }

        // Подключение клиента 2 для получения сообщения
        val client2Job = scope.async {
            val client = createClient { install(WebSockets) }
            client.webSocket("/chat/$surveyId/$email2/$email1") {
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                receivedMessagesClient2.add(text)
                            }
                            else -> fail("Unexpected frame type")
                        }
                    }
                } catch (e: Exception) {
                    // Обработка ошибок
                }
            }
        }

        // Подключение клиента 3 для проверки, что он не получит сообщение
        val client3Job = scope.async {
            val client = createClient { install(WebSockets) }
            client.webSocket("/chat/$surveyId/$email3/$email1") {
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                receivedMessagesClient3.add(text)
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

        // Отправка сообщения после установки соединений
        client1Job.start()
        client2Job.start()
        client3Job.start()

        // Ждем получения сообщения от клиента 1 к клиенту 2 (максимум 3 секунды)
        withTimeoutOrNull(3000) {
            while (receivedMessagesClient2.isEmpty()) {
                delay(100)
            }
        }

        // Проверяем результаты
        assertEquals("Hello User 2!", receivedMessagesClient2.firstOrNull())
        assertTrue(receivedMessagesClient3.isEmpty(), "Client 3 should not receive any messages.")

        // Отменяем задачи
        client1Job.cancel()
        client2Job.cancel()
        client3Job.cancel()
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


            setupPlugins()
            launch { setupDatabase() }
            configureSecurity(appComponent.getJwtService())
            setupChatRouting(appComponent = appComponent) // Подключите вашу маршрутизацию
        }
    }

    @Before
    fun setupDatabase() {
        // Подключение к in-memory базе данных H2
        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    }

}