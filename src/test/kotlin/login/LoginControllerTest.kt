package login

import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.login.domain.models.UserModel
import com.polls_example.feature.login.presentation.LoginController
import com.polls_example.feature.login.presentation.PasswordValidator
import com.polls_example.feature.login.presentation.dto.ConfirmEmailDto
import com.polls_example.feature.login.presentation.dto.LoginDto
import com.polls_example.feature.login.presentation.dto.RegistrationDto
import com.polls_example.legacy.EmailService
import com.polls_example.legacy.cache.SimpleCacheProvider
import com.polls_example.security.JwtService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoginControllerTest {

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val jwtService = mockk<JwtService>(relaxed = true)
    private val emailService = mockk<EmailService>(relaxed = true)
    private val cacheProvider = mockk<SimpleCacheProvider>(relaxed = true)
    private val passwordValidator = object : PasswordValidator {
        override fun validatePassword(password: String, hashPassword: String): Boolean = true
    }

    private val loginController =
        LoginController(userRepository, jwtService, emailService, cacheProvider, passwordValidator)

    @Test
    fun `test authenticate returns LoginResponse on valid credentials`() = runBlocking {
        val loginRequest = LoginDto("test@example.com", "password")
        val foundUser = UserModel(0, "test", "test@example.com", password = "hashedpassword", image = null)

        coEvery { userRepository.userByEmail(loginRequest.email) } returns foundUser
        coEvery { jwtService.createAccessToken(foundUser) } returns "accessToken"
        coEvery { jwtService.createRefreshToken(foundUser) } returns "refreshToken"
        coEvery { userRepository.checkUser(any(), any()) } returns foundUser

        val result = loginController.authenticate(loginRequest)

        assertEquals("accessToken", result?.accessToken)
        assertEquals("refreshToken", result?.refreshToken)
        coVerify { userRepository.userByEmail(loginRequest.email) }
    }

    @Test
    fun `test authenticate returns null on invalid credentials`() = runBlocking {
        val loginRequest = LoginDto("test@example.com", "wrongpassword")

        coEvery { userRepository.userByEmail(loginRequest.email) } returns null

        val result = loginController.authenticate(loginRequest)

        assertNull(result)
        coVerify { userRepository.userByEmail(loginRequest.email) }
    }

    @Test
    fun `test registerUser returns LoginResponse`() = runBlocking {
        val registrationDto = RegistrationDto("Test User", "test@example.com", "password", "imageUrl")
        val user = UserModel(0, "Test User", "test@example.com", null, "hashedPassword")

        coEvery { userRepository.addUser(any()) } returns user
        coEvery { jwtService.createAccessToken(user) } returns "accessToken"
        coEvery { jwtService.createRefreshToken(user) } returns "refreshToken"

        val result = loginController.registerUser(registrationDto)

        assertEquals("accessToken", result.accessToken)
        assertEquals("refreshToken", result.refreshToken)
        coVerify { userRepository.addUser(any()) }
    }

    @Test
    fun `test checkEmailConfirm returns true for valid code`() = runBlocking {
        val confirmEmailDto = ConfirmEmailDto("test@example.com", 123456)

        coEvery { cacheProvider.getCache(confirmEmailDto.email) } returns 123456
//        coEvery { userRepository.confirmEmail(confirmEmailDto.email) } just Runs

        val result = loginController.checkEmailConfirm(confirmEmailDto)

        assertEquals(true, result)
        coVerify { userRepository.confirmEmail(confirmEmailDto.email) }
    }

    @Test
    fun `test checkEmailConfirm returns false for invalid code`() = runBlocking {
        val confirmEmailDto = ConfirmEmailDto("test@example.com", 654321)

        coEvery { cacheProvider.getCache(confirmEmailDto.email) } returns 123456

        val result = loginController.checkEmailConfirm(confirmEmailDto)

        assertEquals(false, result)
        coVerify(exactly = 0) { userRepository.confirmEmail(any()) }
    }

    @Test
    fun `test resetPassword calls userRepository`() = runBlocking {
        val loginDto = LoginDto("test@example.com", "newpassword")

        loginController.resetPassword(loginDto)

        coVerify { userRepository.resetPassword(loginDto.email, loginDto.password) }
    }

    @Test
    fun `test refreshToken returns null for invalid token`() = runBlocking {
        val invalidToken = "invalidRefreshToken"

        coEvery { jwtService.jwtVerifier.verify(invalidToken) } throws Exception("Invalid token")

        val result = loginController.refreshToken(invalidToken)

        assertNull(result)
    }
}