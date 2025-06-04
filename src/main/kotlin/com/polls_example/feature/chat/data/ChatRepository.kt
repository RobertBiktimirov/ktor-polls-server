package com.polls_example.feature.chat.data

import com.polls_example.database.*
import com.polls_example.feature.chat.domain.models.ChatItemModel
import com.polls_example.feature.chat.domain.models.ChatMessageModel
import com.polls_example.feature.chat.domain.models.ChatModel
import com.polls_example.feature.login.domain.models.UserModel
import com.polls_example.legacy.suspendTransaction
import com.polls_example.legacy.timeInMillis
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ChatRepository {

    suspend fun setReadMessage(chatMessageModel: ChatMessageModel) = suspendTransaction {
        val messageDao = ChatMessageDAO.find { ChatMessageTable.id eq chatMessageModel.id }.singleOrNull()
        messageDao?.let {
            messageDao.isRead = true
            messageDao.flush()
        }
    }

    suspend fun getChatModel(
        surveyId: Int,
        senderData: UserModel,
        correspondentData: UserModel,
        page: Int,
    ): ChatModel = suspendTransaction {
        var chatDao = ChatDAO
            .find {
                (ChatTable.userId eq senderData.id) and
                        (ChatTable.surveyId eq surveyId)
            }
            .singleOrNull()

        if (chatDao == null) {
            chatDao = ChatDAO.new {
                userId = senderData.id
                this.surveyId = surveyId
                createdAt = LocalDateTime.now()
                corespondentId = correspondentData.id
            }
        }

        val messages = ChatMessageDAO
            .find { ChatMessageTable.chatId eq chatDao.id.value }
            .orderBy(ChatMessageTable.createdAt to SortOrder.DESC)
            .limit(15, offset = (page - 1) * 15L)
            .toList()

        val survey = SurveyDAO.findById(surveyId)

        return@suspendTransaction ChatModel(
            id = chatDao.id.value,
            surveyId = surveyId,
            surveyTheme = survey?.title,
            correspondentId = correspondentData.id,
            messages = messages.map {
                ChatMessageModel(
                    id = it.id.value,
                    chatId = chatDao.id.value,
                    surveyId = surveyId,
                    senderEmail = senderData.email,
                    senderName = senderData.name,
                    senderAvatarUrl = senderData.image,
                    isUserMessage = it.senderId == senderData.id,
                    textMessage = it.text ?: "",
                    correspondentId = correspondentData.id,
                    timeSend = (it.createdAt?.timeInMillis),
                    senderId = it.senderId
                )
            }
        )
    }

    suspend fun getChats(userId: Int): List<ChatItemModel> = suspendTransaction {
        val chatsDao = ChatDAO
            .find { ChatTable.userId eq userId }
            .toList()

        return@suspendTransaction chatsDao.map {
            val lastMessage = ChatMessageDAO
                .find { ChatMessageTable.chatId eq it.id.value }
                .lastOrNull()

            val isReadLastMessage = lastMessage?.isRead == true
            val lastMessageText = lastMessage?.text
            val surveyId = it.surveyId
            val corespondentId = it.corespondentId

            ChatItemModel(
                id = it.id.value,
                lastMessageText = lastMessageText,
                surveyId = surveyId,
                corespondentId = corespondentId,
                isReadLastMessage = isReadLastMessage,
                timeLastMessage = lastMessage?.createdAt?.timeInMillis,
            )
        }
    }

    suspend fun sendMessage(chatMessageModel: ChatMessageModel) = suspendTransaction {
        println("================send message==================")

        println(chatMessageModel)

        val user = UserDAO
            .find { UsersTable.email eq chatMessageModel.senderEmail }
            .singleOrNull()
            ?: return@suspendTransaction

        val userFromId = user.id.value
        val userToId = chatMessageModel.correspondentId


        var chatDao = ChatDAO
            .find {
                (ChatTable.userId eq userFromId) and
                        (ChatTable.surveyId eq chatMessageModel.surveyId)
            }
            .singleOrNull()

        var chatDao2 = ChatDAO
            .find {
                (ChatTable.userId eq userToId) and
                        (ChatTable.surveyId eq chatMessageModel.surveyId)
            }
            .singleOrNull()

        println("chatDao = $chatDao, chatDao2 = $chatDao2")

        if (chatDao == null) {
            println("create chatDao userFromId = $userFromId, surveyId = ${chatMessageModel.surveyId}, userToId = $userToId")
            chatDao = ChatDAO.new {
                userId = userFromId
                this.surveyId = chatMessageModel.surveyId
                createdAt = LocalDateTime.now()
                corespondentId = userToId
            }
        }
        if (chatDao2 == null) {
            println("create chatDao2 userToId = $userToId, surveyId = ${chatMessageModel.surveyId}, userFromId = $userFromId")
            chatDao2 = ChatDAO.new {
                userId = userToId
                this.surveyId = chatMessageModel.surveyId
                createdAt = LocalDateTime.now()
                corespondentId = userFromId
            }
        }

        println("chatDao = $chatDao, chatDao2 = $chatDao2")

        val instant = Instant.ofEpochMilli(chatMessageModel.timeSend ?: 0)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        ChatMessageDAO.new {
            senderId = chatMessageModel.senderId
            chatId = chatDao.id.value
            text = chatMessageModel.textMessage
            imageUrl = ""
            isRead = true
            createdAt = localDateTime
        }

        ChatMessageDAO.new {
            senderId = chatMessageModel.senderId
            chatId = chatDao2.id.value
            text = chatMessageModel.textMessage
            imageUrl = ""
            isRead = false
            createdAt = localDateTime
        }
    }
}