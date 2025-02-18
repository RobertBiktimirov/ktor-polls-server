package com.polls_example.ioc

import com.polls_example.AppConfig
import com.polls_example.feature.chat.presentation.ChatController
import com.polls_example.feature.chat.presentation.server.TwoPersonChatServer
import com.polls_example.feature.login.presentation.LoginController
import com.polls_example.feature.survey.presentation.question.QuestionController
import com.polls_example.feature.survey.presentation.survey.SurveyController
import com.polls_example.security.JwtService
import scout.Component

class AppComponent(config: AppConfig) : Component(appScope(config)) {
    fun getLoginController(): LoginController = get()
    fun getSurveyController(): SurveyController = get()

    fun getQuestionController(): QuestionController = get()
    fun getJwtService(): JwtService = get()

    fun getTwoPersonChatServer(): TwoPersonChatServer = get()

    fun getChatController(): ChatController = get()
}