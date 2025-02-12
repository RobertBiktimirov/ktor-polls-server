package com.polls_example

import io.ktor.server.config.*

data class AppConfig(
    val jwtConfig: JwtConfig,
    val databaseConfig: DatabaseConfig,
    val emailConfig: EmailConfig,
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
)

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
)

data class EmailConfig(
    val address: String,
    val password: String,
    val from: String,
    val hostName: String,
    val port: Int,
)

fun ApplicationConfig.toAppConfig() = AppConfig(
    jwtConfig = toJwtConfig(),
    databaseConfig = toDatabaseConfig(),
    emailConfig = toEmailConfig(),
)

fun ApplicationConfig.toJwtConfig() = JwtConfig(
    secret = property("jwt.secret").getString(),
    issuer = property("jwt.issuer").getString(),
    audience = property("jwt.audience").getString(),
    realm = property("jwt.realm").getString(),
)

fun ApplicationConfig.toDatabaseConfig() = DatabaseConfig(
    url = property("postgres.url").getString(),
    user = property("postgres.user").getString(),
    password = property("postgres.password").getString()
)

fun ApplicationConfig.toEmailConfig() = EmailConfig(
    address = property("email.address").getString(),
    password = property("email.password").getString(),
    from = property("email.from").getString(),
    hostName = property("email.host_name").getString(),
    port = property("email.port").getString().toIntOrNull() ?: 0
)
