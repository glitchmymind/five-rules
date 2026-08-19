package com.fiverules.server.core

import java.nio.file.Files
import java.nio.file.Paths

object AppConfig {
    const val SERVICE_NAME = "five-rules-api"
    const val VERSION = "0.1.0"

    val host: String = System.getenv("HOST") ?: "0.0.0.0"
    val port: Int = System.getenv("PORT")?.toIntOrNull() ?: 8080

    val jdbcUrl: String by lazy {
        System.getenv("DATABASE_URL")?.trim()?.takeIf { it.isNotEmpty() }
            ?: "jdbc:postgresql://$dbHost:$dbPort/$dbName"
    }

    val dbHost: String by lazy {
        System.getenv("PG_HOST")?.trim()?.takeIf { it.isNotEmpty() } ?: "localhost"
    }
    val dbPort: String by lazy {
        System.getenv("PG_PORT")?.trim()?.takeIf { it.isNotEmpty() } ?: "5432"
    }
    val dbName: String by lazy {
        System.getenv("DB_NAME")?.trim()?.takeIf { it.isNotEmpty() } ?: "five_rules"
    }
    val dbUser: String by lazy {
        readSecret(System.getenv("DB_USER_FILE"))
            ?: System.getenv("DATABASE_USER")?.trim()?.takeIf { it.isNotEmpty() }
            ?: System.getenv("PG_USER_NAME")?.trim()?.takeIf { it.isNotEmpty() }
            ?: "postgres"
    }
    val dbPassword: String by lazy {
        readSecret(System.getenv("DB_PASSWORD_FILE"))
            ?: System.getenv("DATABASE_PASSWORD")?.trim()?.takeIf { it.isNotEmpty() }
            ?: System.getenv("PG_PASSWORD")?.trim()?.takeIf { it.isNotEmpty() }
            ?: "postgres"
    }

    val jwtSecret: String by lazy {
        readSecret(System.getenv("JWT_SECRET_FILE"))
            ?: System.getenv("JWT_SECRET")?.trim()?.takeIf { it.isNotEmpty() }
            ?: "five-rules-dev-secret-change-me"
    }
    val jwtIssuer: String by lazy {
        System.getenv("JWT_ISSUER")?.trim()?.takeIf { it.isNotEmpty() } ?: "five-rules"
    }
    val jwtAudience: String by lazy {
        System.getenv("JWT_AUDIENCE")?.trim()?.takeIf { it.isNotEmpty() } ?: "five-rules-app"
    }

    val resendApiKey: String? by lazy {
        readSecret(System.getenv("RESEND_API_KEY_FILE"))
            ?: System.getenv("RESEND_API_KEY")?.trim()?.takeIf { it.isNotEmpty() }
            ?: readLocalSecret("resend_api_key.txt")
    }
    val emailFrom: String by lazy {
        readSecret(System.getenv("EMAIL_FROM_FILE"))
            ?: System.getenv("EMAIL_FROM")?.trim()?.takeIf { it.isNotEmpty() }
            ?: readLocalSecret("email_from.txt")
            ?: "Five Rules <no-reply@fiverules.app>"
    }
    val emailProvider: String by lazy {
        System.getenv("EMAIL_PROVIDER")?.trim()?.lowercase()
            ?: if (resendApiKey != null) "resend" else "console"
    }

    private fun readSecret(path: String?): String? =
        path?.let {
            runCatching { Files.readString(Paths.get(it)).trim() }
                .getOrNull()
                ?.takeIf { value -> value.isNotEmpty() }
        }

    private fun readLocalSecret(fileName: String): String? {
        val userDir = System.getProperty("user.dir") ?: "."
        val candidates = listOf(
            Paths.get("docker/secrets", fileName),
            Paths.get("secrets", fileName),
            Paths.get(userDir, "docker/secrets", fileName),
            Paths.get(userDir, "secrets", fileName),
        )
        return candidates
            .map { it.normalize().toAbsolutePath() }
            .distinct()
            .firstNotNullOfOrNull { path -> readSecret(path.toString()) }
    }
}
