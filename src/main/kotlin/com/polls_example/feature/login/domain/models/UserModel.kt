package com.polls_example.feature.login.domain.models

import kotlin.random.Random

data class UserModel(
    val id: Int = Random.nextInt(),
    val name: String,
    val email: String,
    val image: String?,
    val password: String,
    val emailVerifiedAt: Long?=null,
    val lastActivity: Long?=null,
    val createdAt: Long?=null,
    val updatedAt: Long?=null,
)
