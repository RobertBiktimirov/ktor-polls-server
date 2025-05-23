package com.polls_example.feature.chat.presentation.mapper

import com.polls_example.feature.chat.domain.models.ChatMessageModel
import kotlinx.serialization.json.Json

class ChatMessageModelMapper {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun toJson(chatModel: ChatMessageModel): String {
        return json.encodeToString(chatModel)
    }

    fun fromJson(jsonString: String): ChatMessageModel? = runCatching<ChatMessageModel> {
        json.decodeFromString(jsonString)
    }.fold(
        onSuccess = { it },
        onFailure = { null }
    )
}

