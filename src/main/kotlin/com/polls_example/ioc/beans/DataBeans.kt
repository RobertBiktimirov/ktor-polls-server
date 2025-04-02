package com.polls_example.ioc.beans

import com.polls_example.feature.chat.data.ChatRepository
import com.polls_example.feature.chat.presentation.server.TwoPersonChatServer
import com.polls_example.feature.files.data.FileRepository
import com.polls_example.feature.login.data.repository.RefreshTokenRepository
import com.polls_example.feature.login.data.repository.UserRepository
import com.polls_example.feature.profile.data.GroupingRepository
import com.polls_example.feature.survey.data.repository.SurveyRepository
import com.polls_example.legacy.cache.SimpleCacheProvider
import com.polls_example.legacy.cache.SimpleRedisCacheProvider
import com.polls_example.legacy.EmailService
import com.polls_example.security.JwtService
import scout.definition.Registry

fun Registry.useDataBeans() {
    singleton<UserRepository> { UserRepository() }

    singleton<JwtService> {
        JwtService(userRepository = get(), jwtConfig = get())
    }

    singleton<RefreshTokenRepository> { RefreshTokenRepository() }

    singleton<SurveyRepository> { SurveyRepository() }

    singleton<EmailService> { EmailService(emailConfig = get()) }
    singleton<SimpleCacheProvider> { SimpleRedisCacheProvider(config = SimpleRedisCacheProvider.Config()) }
    singleton<TwoPersonChatServer> { TwoPersonChatServer() }
    singleton<ChatRepository> { ChatRepository() }
    singleton<GroupingRepository> { GroupingRepository() }
    singleton<FileRepository> { FileRepository() }
}