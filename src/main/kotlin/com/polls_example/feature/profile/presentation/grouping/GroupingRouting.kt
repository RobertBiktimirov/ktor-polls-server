package com.polls_example.feature.profile.presentation.grouping

import com.polls_example.ioc.AppComponent
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.setupGroupingRouting(appComponent: AppComponent) {

    val controller = appComponent.getGroupingController()

    routing {
        authenticate("another-auth") {

        }
    }
}