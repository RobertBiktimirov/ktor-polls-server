package com.polls_example.feature.chat.data

import com.polls_example.database.*
import com.polls_example.legacy.suspendTransaction
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime

class ChatRepository {

    suspend fun sendMessage(
        emailFrom: String,
        surveyId: Int,
        message: String,
    ) = suspendTransaction {
        val user = UserDAO.find { UsersTable.email eq emailFrom }.singleOrNull() ?: return@suspendTransaction
        val userFromId = user.id.value
        var chatDao = ChatDAO
            .find { (ChatTable.userId eq userFromId) and (ChatTable.surveyId eq surveyId) }
            .singleOrNull()

        if (chatDao == null) {
            chatDao = ChatDAO.new {
                userId = userFromId
                this.surveyId = surveyId
                createdAt = LocalDateTime.now()
            }
        }

        ChatMessageDAO.new {
            senderId = userFromId
            chatId = chatDao.id.value
            text = message
            imageUrl = ""
            isRead = false
            createdAt = LocalDateTime.now()
        }
    }
}