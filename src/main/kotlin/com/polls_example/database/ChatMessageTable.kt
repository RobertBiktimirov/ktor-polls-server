package com.polls_example.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object ChatMessageTable : IntIdTable("chat_messages") {
    val senderId = integer("sender_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val chatId = integer("chat_id").references(ChatTable.id, onDelete = ReferenceOption.CASCADE)
    val text = text("text").nullable()
    val imageUrl = varchar("image_url", 255).nullable()
    val isRead = bool("is_read")
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable().default(null)
}

class ChatMessageDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<ChatMessageDAO>(ChatMessageTable)

    var senderId by ChatMessageTable.senderId
    var chatId by ChatMessageTable.chatId
    var text by ChatMessageTable.text
    var imageUrl by ChatMessageTable.imageUrl
    var isRead by ChatMessageTable.isRead
    var createdAt by ChatMessageTable.createdAt
    var updatedAt by ChatMessageTable.updatedAt
}