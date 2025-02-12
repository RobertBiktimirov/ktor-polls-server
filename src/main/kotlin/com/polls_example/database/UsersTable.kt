package com.polls_example.database

import com.polls_example.feature.login.domain.models.UserModel
import com.polls_example.legacy.timeInMillis
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : IntIdTable("users") {
    val name = varchar("name", 128)
    val email = varchar("email", 128).uniqueIndex()
    val password = varchar("password", 256)
    val image = varchar("image", 256).nullable()
    val emailVerifiedAt = datetime("email_verified_at").nullable()
    val lastActivity = datetime("last_activity").nullable()
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<UserDAO>(UsersTable)

    var name by UsersTable.name
    var email by UsersTable.email
    var password by UsersTable.password
    var image by UsersTable.image
    var emailVerifiedAt by UsersTable.emailVerifiedAt
    val lastActivity by UsersTable.lastActivity
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt
}

fun UserDAO.toModel(): UserModel = UserModel(
    id = id.value,
    name = name,
    email = email,
    image = image,
    password = password,
    emailVerifiedAt = emailVerifiedAt?.timeInMillis,
    lastActivity = lastActivity?.timeInMillis,
    createdAt = createdAt?.timeInMillis,
    updatedAt = updatedAt?.timeInMillis,
)