package com.polls_example.legacy

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun hashPassword(password: String): String {
    val passwordEncoder = BCryptPasswordEncoder()
    val newHashPassword = passwordEncoder.encode(password)
    return newHashPassword
}

fun verificationPassword(password: String, hashPassword: String): Boolean {
    val passwordEncoder = BCryptPasswordEncoder()
    return passwordEncoder.matches(password, hashPassword)
}
