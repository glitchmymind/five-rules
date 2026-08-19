package com.fiverules.server.gateway

import com.fiverules.server.auth.routes.authRoutes
import com.fiverules.server.feed.routes.feedRoutes
import com.fiverules.server.gateway.routes.healthRoutes
import com.fiverules.server.rules.routes.rulesRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            healthRoutes()
            authRoutes()
            rulesRoutes()
            feedRoutes()
        }
    }
}
