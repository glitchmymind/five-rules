package com.fiverules.server.gateway.routes

import com.fiverules.server.core.AppConfig
import com.fiverules.server.gateway.HealthResponse
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes() {
    get("/health") {
        call.respond(
            HealthResponse(
                status = "ok",
                service = AppConfig.SERVICE_NAME,
                version = AppConfig.VERSION,
            ),
        )
    }
}
