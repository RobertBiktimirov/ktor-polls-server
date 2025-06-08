//package files
//
//import com.polls_example.configureSecurity
//import com.polls_example.feature.files.presentation.Paths
//import com.polls_example.feature.files.presentation.setupFileUploadRouting
//import com.polls_example.ioc.AppComponent
//import com.polls_example.setupPlugins
//import com.polls_example.toAppConfig
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.*
//import io.ktor.http.*
//import io.ktor.http.content.*
//import io.ktor.server.config.*
//import io.ktor.server.testing.*
//import io.ktor.utils.io.jvm.javaio.*
//import junit.framework.TestCase.assertEquals
//import org.junit.Before
//import java.io.File
//import kotlin.test.Test
//import kotlin.test.assertTrue
//
//class FilesTest {
//
//    @Test
//    fun testUploadAvatar() = testApplication {
//        setupFileTest()
//
//        val file = File("src/test/kotlin/com/polls_example/files/test_image/client_file/image.jpeg")
//        val multiPartData = MultiPartFormDataContent(createMultiPartFormData(file))
//
//        val response = client.post("/upload/avatar") {
//            header(HttpHeaders.ContentType, "multipart/form-data")
//            setBody(multiPartData)
//        }
//
//        assertEquals(HttpStatusCode.OK, response.status)
//
//        val uploadedFile = File("${Paths.RESOURCE_AVATARS_PATH}/test_avatar.jpg")
//        assertTrue(uploadedFile.exists(), "Uploaded file should exist")
//
//        // Удаляем файл после теста
//        uploadedFile.delete()
//    }
//
//    private fun createMultiPartFormData(file: File): List<PartData> {
//        return listOf(
//            PartData.FileItem(
//                { file.inputStream().toByteReadChannel() }, // правильный тип
//                {  }, // функция для освобождения ресурсов
//                Headers.build { append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString()) } // правильный заголовок
//            )
//        )
//    }
//
//    private fun ApplicationTestBuilder.setupFileTest() {
//        environment {
//            config = MapApplicationConfig(
//                "jwt.secret" to "test_secret",
//                "jwt.issuer" to "test_issuer",
//                "jwt.audience" to "test_audience",
//                "jwt.realm" to "realm example",
//                "postgres.url" to "test_db_url",
//                "postgres.user" to "test_user",
//                "postgres.password" to "test_password",
//                "email.address" to "test_email@example.com",
//                "email.password" to "test_password",
//                "email.from" to "test_from@example.com",
//                "email.host_name" to "test_host",
//                "email.port" to "587"
//            )
//        }
//        application {
//            val appConfig = environment.config.toAppConfig()
//            val appComponent = AppComponent(appConfig)
//
//            setupPlugins()
//            configureSecurity(appComponent.getJwtService())
//            setupFileUploadRouting(component = appComponent) // Подключите вашу маршрутизацию
//        }
//    }
//
//    @Before
//    fun setupDatabase() {
//        // Подключение к in-memory базе данных H2
//        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
//    }
//}