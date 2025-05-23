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
        members[email to surveyId]?.close(CloseReason(CloseReason.Codes.NORMAL, "Reconnected"))
        members[email to surveyId] = socket
    }

    suspend fun sendMessage(emailTo: String, emailFrom: String, surveyId: Int, message: String) {
        val recipientSession = members[emailTo to surveyId]
        val senderSession = members[emailFrom to surveyId]

        recipientSession?.sendMessage(Frame.Text(message))
        senderSession?.sendMessage(Frame.Text(message))
    }

    suspend fun memberLeft(email: String, surveyId: Int) {
        members.remove(email to surveyId)
        sendToOther(email to surveyId, "$email left the chat.")
    }

    private suspend fun sendToOther(excluded: ChatMemberKey, message: String) {
        members.filterKeys { it != excluded }.forEach { (_, socket) ->
//            socket.sendMessage(Frame.Text(message))
        }
    }
}