package com.polls_example.feature.chat.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatModel(
    val id: Int,
    val surveyId: Int,
    val surveyTheme: String?,
    val correspondentId: Int,
    val messages: List<ChatMessageModel>
)

@Serializable
data class ChatMessageModel(
    var id: Int = 0,
    var chatId: Int = 0,
    val surveyId: Int,
    val correspondentId: Int,
    val senderEmail: String,
    val senderId: Int,
    val senderName: String,
    val senderAvatarUrl: String?,
    val timeSend: Long? = null,
    val isUserMessage: Boolean,
    val textMessage: String,
)

data class ChatItemModel(
    val id: Int,
    val lastMessageText: String?,
    val surveyId: Int,
    val corespondentId: Int?,
    val isReadLastMessage: Boolean,
)

@Serializable
data class ChatItemDto(
    val id: Int,
    val survey: SurveyChatItemModel,
    val correspondent: CorrespondentChatModel?,
    val lastMessageText: String?,
    val isReadLastMessages: Boolean = true,
) {

    @Serializable
    data class SurveyChatItemModel(
        val id: Int,
        val theme: String?,
        val imageUrl: String?
    )

    @Serializable
    data class CorrespondentChatModel(
        val id: Int,
        val name: String,
        val email: String,
        val avatarUrl: String?,
    )
}