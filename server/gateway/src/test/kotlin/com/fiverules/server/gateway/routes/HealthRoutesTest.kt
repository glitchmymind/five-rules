package com.fiverules.server.gateway.routes

import com.fiverules.server.auth.plugins.configureAuthentication
import com.fiverules.server.gateway.HealthResponse
import com.fiverules.server.gateway.configureRouting
import com.fiverules.server.network.configureNetwork
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthRoutesTest {
    @Test
    fun `GET health returns ok payload`() = testApplication {
        application {
            configureAuthentication()
            configureNetwork()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/api/v1/health")
        val body = response.body<HealthResponse>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("ok", body.status)
        assertEquals("five-rules-api", body.service)
        assertEquals("0.1.0", body.version)
    }
}
