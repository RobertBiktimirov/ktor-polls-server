package com.polls_example.feature.login.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RefreshTokenRepository {

    private val tokens = mutableMapOf<String, String>()
    private val mutex = Mutex()

    suspend fun findEmailByToken(token: String): String? {
        return mutex.withLock {
            tokens[token]
        }
    }

    suspend fun save(token: String, email: String) {
        mutex.withLock {
            tokens[token] = email
        }
    }
}