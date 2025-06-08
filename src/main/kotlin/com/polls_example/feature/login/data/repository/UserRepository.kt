package com.polls_example.feature.login.data.repository

import com.polls_example.database.UserDAO
import com.polls_example.database.UsersTable
import com.polls_example.database.toModel
import com.polls_example.feature.login.domain.exception.UserException
import com.polls_example.feature.login.domain.models.UserModel
import com.polls_example.feature.profile.presentation.grouping.dto.EditProfileModel
import com.polls_example.legacy.hashPassword
import com.polls_example.legacy.suspendTransaction
import com.polls_example.legacy.verificationPassword
import java.time.LocalDateTime

class UserRepository {

    suspend fun allUsers(): List<UserModel> = suspendTransaction {
        UserDAO.all().map { it.toModel() }
    }

    suspend fun userById(id: Int): UserModel? = suspendTransaction {
        UserDAO.findById(id)?.toModel()
    }

    suspend fun updateUserAvatar(path: String, userId: Int) = suspendTransaction {
        val userDao = UserDAO.findById(userId)
        userDao?.image = path
        userDao?.flush()
    }

    suspend fun updateUserInfo(userId: Int, editProfileModel: EditProfileModel) = suspendTransaction {
        val userDao = UserDAO.findById(userId)
        editProfileModel.name?.let { userDao?.name = it }
        editProfileModel.email?.let { userDao?.email = it }
        editProfileModel.avatarUrl?.let { userDao?.image = it }

        userDao?.flush()
    }

    suspend fun userByEmail(email: String): UserModel? = suspendTransaction {
        val userByEmail = UserDAO
            .find { UsersTable.email eq email }
            .singleOrNull()

        return@suspendTransaction userByEmail?.toModel()
    }

    suspend fun getUsersByEmail(userId: Int, email: String): List<UserModel> = suspendTransaction {
        val users = UserDAO
            .find { UsersTable.email like "$email%" }
            .limit(10)
            .filter { it.id.value != userId }
            .map { it.toModel() }

        users
    }

    suspend fun checkHaveEmail(email: String) = suspendTransaction {
        val userByEmail = UserDAO
            .find { UsersTable.email eq email }
            .singleOrNull()

        userByEmail?.let {
            throw UserException("Почта уже используется")
        }

        return@suspendTransaction
    }

    suspend fun addUser(userModel: UserModel) = suspendTransaction {
        val userByEmail = UserDAO
            .find { UsersTable.email eq userModel.email }
            .singleOrNull()

        userByEmail?.let {
            throw UserException("Почта уже используется")
        }

        val dao = UserDAO.new {
            name = userModel.name
            email = userModel.email
            password = hashPassword(userModel.password)
            image = userModel.image
            createdAt = LocalDateTime.now()
        }

        return@suspendTransaction dao.toModel()
    }

    suspend fun checkUser(email: String, password: String): UserModel = suspendTransaction {
        val userByEmail = UserDAO
            .find { UsersTable.email eq email }
            .singleOrNull() ?: throw UserException("Почта не найдена")

        if (!verificationPassword(password, userByEmail.password)) throw UserException("Неверный пароль")

        return@suspendTransaction userByEmail.toModel()
    }

    suspend fun resetPassword(email: String, newPassword: String) = suspendTransaction {
        val userByEmail = UserDAO
            .find { UsersTable.email eq email }
            .singleOrNull() ?: throw UserException("Почта не найдена")

        userByEmail.password = hashPassword(newPassword)
        userByEmail.flush()
    }

    suspend fun confirmEmail(email: String, isConfirm: Boolean = true): UserModel? = suspendTransaction {
        val userByEmail = UserDAO
            .find { UsersTable.email eq email }
            .singleOrNull()

        userByEmail?.emailVerifiedAt = if (isConfirm) LocalDateTime.now() else null
        userByEmail?.flush()

        userByEmail?.toModel()
    }
}