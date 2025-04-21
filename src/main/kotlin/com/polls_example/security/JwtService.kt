package com.polls_example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.polls_example.JwtConfig
import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.login.domain.models.UserModel
import io.ktor.server.auth.jwt.*
import java.time.Instant

// лучше не хранить
const val CLAIM_EMAIL = "email"
const val CLAIM_NAME = "name"
const val CLAIM_IMAGE_URL = "image_url"
const val CLAIM_EMAIL_VERIFIED_AT = "email_verified_at"
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
            .withClaim(CLAIM_EMAIL, userModel.email)
            .withClaim(CLAIM_NAME, userModel.name)
            .withClaim(CLAIM_IMAGE_URL, userModel.image)
            .withClaim(CLAIM_EMAIL_VERIFIED_AT, userModel.emailVerifiedAt)
            .withExpiresAt(Instant.now().plusMillis(expireIn))
            .sign(Algorithm.HMAC256(secret))

    suspend fun customValidator(
        credential: JWTCredential,
    ): JWTPrincipal? {
        val email: String? = extractEmail(credential)
        val foundUser: UserModel? = email?.let { userRepository.userByEmail(email) }

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

    private fun extractEmail(credential: JWTCredential): String? =
        credential.payload.getClaim(CLAIM_EMAIL).asString()
}