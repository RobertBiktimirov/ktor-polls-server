package com.polls_example.feature.chat.presentation

import com.polls_example.feature.chat.data.ChatRepository
import com.polls_example.feature.chat.domain.models.ChatItemDto
import com.polls_example.feature.chat.domain.models.ChatModel
import com.polls_example.feature.chat.presentation.mapper.ChatMessageModelMapper
import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.survey.data.repository.SurveyRepository

class ChatController(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    private val surveyRepository: SurveyRepository,
) {

    private val jsonMapper = ChatMessageModelMapper()

    suspend fun setReadMessage(json: String): Result<Unit> = kotlin.runCatching {
        val message = jsonMapper.fromJson(json)
        message?.let { repository.setReadMessage(it) }
    }

    suspend fun sendMessage(json: String): Result<String> = kotlin.runCatching {
        val chatMessageModel = jsonMapper.fromJson(json)
            ?: return Result.failure(IllegalArgumentException("Invalid message format"))

        if (chatMessageModel.textMessage.isBlank()) {
            return Result.failure(IllegalArgumentException("Message text is empty"))
        }

        repository.sendMessage(chatMessageModel)
        return Result.success(jsonMapper.toJson(chatMessageModel))
    }

    suspend fun getChatMessageModel(
        surveyId: Int,
        idTo: String,
        idFrom: String,
        page: Int
    ): ChatModel {
        val sender = userRepository.userById(idFrom.toInt())
            ?: throw IllegalArgumentException("User $idFrom not found")

        val correspondent = userRepository.userById(idTo.toInt())
            ?: throw IllegalArgumentException("User $idTo not found")

        return repository.getChatModel(
            surveyId = surveyId,
            senderData = sender,
            correspondentData = correspondent,
            page = page
        )
    }

    suspend fun getChats(userId: Int): List<ChatItemDto> {
        val chats = repository.getChats(userId)
        return chats.map { chatItem ->
            val surveyInfo = surveyRepository.getSurveyInfo(userId, chatItem.surveyId, false)
            val correspondentInfo = chatItem.corespondentId?.let { it1 -> userRepository.userById(it1) }

            ChatItemDto(
                id = chatItem.id,
                lastMessageText = chatItem.lastMessageText,
                isReadLastMessages = chatItem.isReadLastMessage,
                survey = ChatItemDto.SurveyChatItemModel(
                    id = surveyInfo?.id ?: 0,
                    theme = surveyInfo?.title,
                    imageUrl = surveyInfo?.imageUrl
                ),
                correspondent = correspondentInfo?.let {
                    ChatItemDto.CorrespondentChatModel(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        avatarUrl = it.image
                    )
                }
            )
        }
    }
}