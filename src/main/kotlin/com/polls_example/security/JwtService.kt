package com.polls_example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.polls_example.JwtConfig
import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.login.domain.models.UserModel
import io.ktor.server.auth.jwt.*
import java.time.Instant

const val CLAIM_USER_ID = "user_id"

class JwtService(
    private val userRepository: UserRepository,
    jwtConfig: JwtConfig
) {
    private val secret = jwtConfig.secret
    private val issuer = jwtConfig.issuer
    private val audience = jwtConfig.audience
    val realm = jwtConfig.realm

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    // 1 Час
    fun createAccessToken(userModel: UserModel): String =
        createJwtToken(userModel, 3600000L)

    // 1 Год
    fun createRefreshToken(userModel: UserModel): String =
        createJwtToken(userModel, 31556952000L)

    private fun createJwtToken(userModel: UserModel, expireIn: Long): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(CLAIM_USER_ID, userModel.id)
            .withExpiresAt(Instant.now().plusMillis(expireIn))
            .sign(Algorithm.HMAC256(secret))

    suspend fun customValidator(
        credential: JWTCredential,
    ): JWTPrincipal? {
        val id: Int = extractUserId(credential)
        val foundUser: UserModel? = userRepository.userById(id)

        return foundUser?.let {
            if (audienceMatches(credential))
                JWTPrincipal(credential.payload)
            else
                null
        }
    }

    private fun audienceMatches(
        credential: JWTCredential,
    ): Boolean =
        credential.payload.audience.contains(audience)

    fun audienceMatches(
        audience: String
    ): Boolean =
        this.audience == audience

    private fun extractUserId(credential: JWTCredential): Int =
        credential.payload.getClaim(CLAIM_USER_ID).asInt()
}