package com.polls_example.ioc

import com.polls_example.AppConfig
import com.polls_example.feature.login.presentation.LoginController
import com.polls_example.feature.survey.presentation.SurveyController
import com.polls_example.security.JwtService
import scout.Component

class AppComponent(config: AppConfig) : Component(appScope(config)) {
    fun getLoginController(): LoginController = get()
    fun getSurveyController(): SurveyController = get()
    fun getJwtService(): JwtService = get()
}