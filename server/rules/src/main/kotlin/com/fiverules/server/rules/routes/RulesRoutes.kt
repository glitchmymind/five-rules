package com.fiverules.server.rules.routes

import com.fiverules.common.models.auth.ApiErrorResponse
import com.fiverules.common.models.rules.AttachTasksRequest
import com.fiverules.common.models.rules.CreateRuleRequest
import com.fiverules.common.models.rules.CreateTaskRequest
import com.fiverules.common.models.rules.RulesResponse
import com.fiverules.server.core.JwtConfig
import com.fiverules.server.rules.RulesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.rulesRoutes() {
    val rulesRepository by inject<RulesRepository>()

    authenticate(JwtConfig.Jwt) {
        get("/rules") {
            call.respond(RulesResponse(rules = rulesRepository.fetchRules()))
        }
        post("/rules") {
            val body = call.receive<CreateRuleRequest>()
            if (body.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_input", "Name is required"))
                return@post
            }
            call.respond(HttpStatusCode.Created, rulesRepository.createRule(body))
        }
        delete("/rules/{id}") {
            val id = call.parameters["id"]?.toUuidOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_id", "Rule id is invalid"))
                return@delete
            }
            if (!rulesRepository.deleteRule(id)) {
                call.respond(HttpStatusCode.NotFound, ApiErrorResponse("not_found", "Rule not found"))
                return@delete
            }
            call.respond(HttpStatusCode.NoContent)
        }
        post("/rules/{id}/tasks") {
            val id = call.parameters["id"]?.toUuidOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_id", "Rule id is invalid"))
                return@post
            }
            val body = call.receive<AttachTasksRequest>()
            val taskIds = body.taskIds.mapNotNull { it.toUuidOrNull() }
            rulesRepository.attachTasks(id, taskIds)
            call.respond(HttpStatusCode.OK)
        }

        get("/tasks") {
            call.respond(rulesRepository.fetchTasks())
        }
        post("/tasks") {
            val body = call.receive<CreateTaskRequest>()
            if (body.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_input", "Name is required"))
                return@post
            }
            call.respond(HttpStatusCode.Created, rulesRepository.createTask(body))
        }
        delete("/tasks/{id}") {
            val id = call.parameters["id"]?.toUuidOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("invalid_id", "Task id is invalid"))
                return@delete
            }
            if (!rulesRepository.deleteTask(id)) {
                call.respond(HttpStatusCode.NotFound, ApiErrorResponse("not_found", "Task not found"))
                return@delete
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()
