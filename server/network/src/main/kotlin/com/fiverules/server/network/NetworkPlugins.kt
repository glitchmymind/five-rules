package com.fiverules.server.network

import com.fiverules.server.core.TracingHeaders
import com.fiverules.server.core.TracingMdc
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

@Serializable
data class ErrorResponse(
    val error: String,
    val requestId: String? = null,
)

fun Application.configureNetwork() {
    configureRequestTracing()

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            },
        )
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc(TracingMdc.REQUEST_ID)
        mdc(TracingMdc.TRACE_ID) { it.requestContextOrNull?.traceId }
        mdc(TracingMdc.SPAN_ID) { it.requestContextOrNull?.spanId }
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(TracingHeaders.REQUEST_ID)
        allowHeader(TracingHeaders.TRACE_ID)
        allowHeader(TracingHeaders.TRACEPARENT)
        exposeHeader(TracingHeaders.REQUEST_ID)
        exposeHeader(TracingHeaders.TRACE_ID)
        exposeHeader(TracingHeaders.TRACEPARENT)
        anyHost()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = cause.deepMessage() ?: "Internal server error",
                    requestId = call.callId,
                ),
            )
        }
    }
}

private fun Throwable.deepMessage(): String? =
    generateSequence(this) { it.cause }
        .mapNotNull { it.message?.trim()?.takeIf(String::isNotEmpty) }
        .distinct()
        .joinToString(" <- ")
        .takeIf { it.isNotEmpty() }
