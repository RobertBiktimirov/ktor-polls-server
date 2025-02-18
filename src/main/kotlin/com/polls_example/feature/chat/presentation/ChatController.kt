package com.polls_example.feature.chat.presentation

import com.polls_example.feature.chat.data.ChatRepository

class ChatController(
    private val repository: ChatRepository,
) {
    suspend fun sendMessage(
        emailFrom: String,
        surveyId: Int,
        message: String,
    ) = repository.sendMessage(emailFrom, surveyId, message)
}