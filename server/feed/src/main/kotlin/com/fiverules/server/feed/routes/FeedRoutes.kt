package com.fiverules.server.feed.routes

import com.fiverules.common.models.auth.ApiErrorResponse
import com.fiverules.common.models.feed.CreateFeedRequest
import com.fiverules.server.core.JwtConfig
import com.fiverules.server.feed.FeedRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.feedRoutes() {
    val feedRepository by inject<FeedRepository>()

    authenticate(JwtConfig.Jwt) {
        get("/feeds") {
            call.respond(feedRepository.fetchAll())
        }
        post("/feeds") {
            val userId = call.principal<JWTPrincipal>()
                ?.payload
                ?.getClaim(JwtConfig.userIdClaim)
                ?.asString()
                ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiErrorResponse("unauthorized", "Missing user"))
                return@post
            }
            val body = call.receive<CreateFeedRequest>()
            if (body.text.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_input", "Text is required"))
                return@post
            }
            val created = feedRepository.create(userId, body)
            if (created == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_input", "User or rule not found"))
                return@post
            }
            call.respond(HttpStatusCode.Created, created)
        }
    }
}
