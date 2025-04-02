package com.polls_example.feature.files.presentation

import com.polls_example.feature.login.data.repository.UserRepository

class FileController(
    private val userRepository: UserRepository,
) {

    suspend fun updateUserAvatar(userId: Int, path: String) = userRepository.updateUserAvatar(path, userId)
}