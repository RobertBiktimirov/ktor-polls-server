package com.polls_example.feature.login.presentation

import com.auth0.jwt.interfaces.DecodedJWT
import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.login.domain.models.UserModel
import com.polls_example.feature.login.presentation.dto.ConfirmEmailDto
import com.polls_example.feature.login.presentation.dto.LoginDto
import com.polls_example.feature.login.presentation.dto.LoginResponse
import com.polls_example.feature.login.presentation.dto.RegistrationDto
import com.polls_example.legacy.EmailService
import com.polls_example.legacy.cache.SimpleCacheProvider
import com.polls_example.security.CLAIM_EMAIL
import com.polls_example.security.JwtService
import kotlin.random.Random

interface PasswordValidator {
    fun validatePassword(password: String, hashPassword: String): Boolean
}

class LoginController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val emailService: EmailService,
    private val cacheProvider: SimpleCacheProvider,
    private val passwordValidator: PasswordValidator
) {
    suspend fun authenticate(loginRequest: LoginDto): LoginResponse? {
        checkUser(loginRequest.email, loginRequest.password)

        val email = loginRequest.email
        val foundUser: UserModel? = userRepository.userByEmail(email)

        return if (foundUser != null && passwordValidator.validatePassword(loginRequest.password, foundUser.password)) {
            val accessToken = jwtService.createAccessToken(foundUser)
            val refreshToken = jwtService.createRefreshToken(foundUser)
            LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } else
            null
    }

    suspend fun refreshToken(token: String): String? {
        val decodedRefreshToken = verifyRefreshToken(token)
        return if (decodedRefreshToken != null) {
            val emailFromRefreshToken: String = decodedRefreshToken.getClaim(CLAIM_EMAIL).asString()
            val foundUser: UserModel? = userRepository.userByEmail(emailFromRefreshToken)
            if (foundUser != null)
                jwtService.createAccessToken(foundUser)
            else
                null
        } else
            null
    }

    suspend fun registerUser(registrationDto: RegistrationDto): LoginResponse {
        val userModel = userRepository.addUser(
            UserModel(
                name = registrationDto.name,
                email = registrationDto.email,
                image = registrationDto.image,
                password = registrationDto.password
            )
        )

        val accessToken = createAccessToken(userModel)
        val refreshToken = createRefreshToken(userModel)

//        sendEmailCode(userModel.email)
        return LoginResponse(accessToken, refreshToken)
    }

    suspend fun checkEmailConfirm(confirmEmailDto: ConfirmEmailDto): Boolean {
        val redisCode = cacheProvider.getCache(confirmEmailDto.email)?.toString()?.toIntOrNull()
        val isEquals = redisCode == confirmEmailDto.code

        println("user redis code = $redisCode")
        println("user confirm code = ${confirmEmailDto.code}")

        if (isEquals) userRepository.confirmEmail(confirmEmailDto.email)
        return isEquals
    }

    suspend fun sendEmailCode(email: String) {
        val code = Random.nextInt(100000, 999999)
        if (emailService.setConfirmEmailMessage(email, code)) {
            cacheProvider.setCache(email, code)
        }
    }

    private fun createAccessToken(user: UserModel) = jwtService.createAccessToken(user)
    private fun createRefreshToken(user: UserModel) = jwtService.createRefreshToken(user)

    private fun verifyRefreshToken(token: String): DecodedJWT? {
        val decodedJwt: DecodedJWT? = getDecodedJwt(token)
        return decodedJwt?.let {
            val audienceMatches = jwtService.audienceMatches(it.audience.first())
            if (audienceMatches)
                decodedJwt
            else
                null
        }
    }

    private fun getDecodedJwt(token: String): DecodedJWT? =
        try {
            jwtService.jwtVerifier.verify(token)
        } catch (ex: Exception) {
            null
        }

    private suspend fun checkUser(email: String, password: String): UserModel =
        userRepository.checkUser(email, password)

    suspend fun resetPassword(loginDto: LoginDto) {
        userRepository.resetPassword(loginDto.email, loginDto.password)
    }
}