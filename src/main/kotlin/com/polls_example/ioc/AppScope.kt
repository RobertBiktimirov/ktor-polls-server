package com.polls_example.ioc

import com.polls_example.AppConfig
import com.polls_example.EmailConfig
import com.polls_example.JwtConfig
import com.polls_example.ioc.beans.useDataBeans
import com.polls_example.ioc.beans.usePresentationBeans
import scout.scope

const val APP_SCOPE = "app_scope"

fun appScope(config: AppConfig) = scope(APP_SCOPE) {
    singleton<AppConfig> { config }
    singleton<JwtConfig> { config.jwtConfig }
    singleton<EmailConfig> { config.emailConfig }

    useDataBeans()
    usePresentationBeans()
}