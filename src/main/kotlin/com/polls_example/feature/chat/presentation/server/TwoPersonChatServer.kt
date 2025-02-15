package com.polls_example.feature.chat.presentation.server

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

typealias ChatMemberKey = Pair<String, Int>

class TwoPersonChatServer {

    /**
     * key - email users
     */
    private val members = ConcurrentHashMap<ChatMemberKey, WebSocketServerSession>()

    /**
     * Поддержка отправки сообщений и закрытие сокетов.
     */
    private suspend fun WebSocketServerSession.sendMessage(frame: Frame) {
        try {
            this.send(frame)
        } catch (e: Throwable) {
            this.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "An error occurred while sending message"))
        }
    }

    suspend fun memberJoin(email: String, surveyId: Int, socket: WebSocketServerSession) {
        members[email to surveyId] = socket
        sendToOther(email to surveyId, "$email joined the chat")
    }

    suspend fun memberLeft(email: String, surveyId: Int) {
        members.remove(email to surveyId)
        sendToOther(email to surveyId, "$email left the chat.")
    }

    suspend fun sendMessage(email: String, surveyId: Int, message: String) {
        members[email to surveyId]?.sendMessage(Frame.Text(message))
    }

    private suspend fun sendToOther(excluded: ChatMemberKey, message: String) {
        members.filterKeys { it != excluded }.forEach { (_, socket) ->
//            socket.sendMessage(Frame.Text(message))
        }
    }
}