package com.fiverules.server.auth

import com.fiverules.server.core.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class EmailService {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val client = HttpClient(CIO) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun sendVerificationCode(email: String, code: String) {
        sendCodeEmail(
            email = email,
            subject = "Код подтверждения Five Rules",
            html = verificationEmailHtml(code),
            text = "Ваш код подтверждения Five Rules: $code. Он действует 10 минут.",
            logLabel = "verification",
            code = code,
        )
    }

    suspend fun sendPasswordResetCode(email: String, code: String) {
        sendCodeEmail(
            email = email,
            subject = "Код для сброса пароля Five Rules",
            html = passwordResetEmailHtml(code),
            text = "Код для сброса пароля Five Rules: $code. Он действует 10 минут.",
            logLabel = "password reset",
            code = code,
        )
    }

    private suspend fun sendCodeEmail(
        email: String,
        subject: String,
        html: String,
        text: String,
        logLabel: String,
        code: String,
    ) {
        if (AppConfig.emailProvider == "console") {
            logger.info("EMAIL_PROVIDER=console; {} code for {}: {}", logLabel, email, code)
            return
        }
        require(AppConfig.emailProvider == "resend") {
            "Unsupported EMAIL_PROVIDER=${AppConfig.emailProvider}"
        }
        val apiKey = requireNotNull(AppConfig.resendApiKey) {
            "RESEND_API_KEY is required when EMAIL_PROVIDER=resend"
        }
        val configuredFrom = AppConfig.emailFrom
        val first = postResend(apiKey, configuredFrom, email, subject, html, text, logLabel)
        if (first.isSuccess) return
        val needsOnboardingSender = first.needsOnboardingSender && configuredFrom != RESEND_ONBOARDING_FROM
        if (needsOnboardingSender) {
            logger.warn("Resend rejected {} from {}. Retrying with {}", logLabel, configuredFrom, RESEND_ONBOARDING_FROM)
            val retry = postResend(apiKey, RESEND_ONBOARDING_FROM, email, subject, html, text, logLabel)
            if (retry.isSuccess) return
            logger.info("EMAIL_PROVIDER fallback to console; {} code for {}: {}", logLabel, email, code)
            error(retry.message)
        }
        logger.info("EMAIL_PROVIDER fallback to console; {} code for {}: {}", logLabel, email, code)
        error(first.message)
    }

    private suspend fun postResend(
        apiKey: String,
        from: String,
        email: String,
        subject: String,
        html: String,
        text: String,
        logLabel: String,
    ): ResendAttempt {
        logger.info("Sending {} email via Resend from {} to {}", logLabel, from, email)
        val response = client.post("https://api.resend.com/emails") {
            bearerAuth(apiKey)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(
                ResendEmailRequest(
                    from = from,
                    to = listOf(email),
                    subject = subject,
                    html = html,
                    text = text,
                ),
            )
        }
        val details = response.bodyAsText()
        if (response.status.isSuccess()) {
            logger.info("Resend accepted {} email to {}", logLabel, email)
            return ResendAttempt(isSuccess = true, message = details, needsOnboardingSender = false)
        }
        logger.error("Resend rejected {} email: {} {}", logLabel, response.status, details)
        val needsOnboardingSender = details.contains("not verified", ignoreCase = true) ||
            details.contains("domain is not", ignoreCase = true)
        return ResendAttempt(
            isSuccess = false,
            message = "Resend rejected $logLabel email: ${response.status} $details",
            needsOnboardingSender = needsOnboardingSender,
        )
    }

    private data class ResendAttempt(
        val isSuccess: Boolean,
        val message: String,
        val needsOnboardingSender: Boolean,
    )

    private companion object {
        const val RESEND_ONBOARDING_FROM = "Five Rules <beth.t@example.com>"
    }
}

private fun verificationEmailHtml(code: String): String = emailShell(
    title = "Код подтверждения",
    heading = "Подтвердите почту",
    intro = "Введите этот код в Five Rules, чтобы завершить регистрацию.",
    code = code,
)

private fun passwordResetEmailHtml(code: String): String = emailShell(
    title = "Сброс пароля",
    heading = "Сброс пароля",
    intro = "Введите код в Five Rules, чтобы задать новый пароль.",
    code = code,
)

private fun emailShell(title: String, heading: String, intro: String, code: String): String = """
    <!DOCTYPE html>
    <html lang="ru">
    <head><meta charset="utf-8" /><title>$title</title></head>
    <body style="margin:0;padding:0;background-color:#F7FBF7;">
      <table role="presentation" width="100%" style="background-color:#F7FBF7;">
        <tr><td align="center" style="padding:32px 16px;">
          <table role="presentation" width="100%" style="max-width:560px;background:#FFFFFF;border-radius:12px;">
            <tr>
              <td align="center" style="background-color:#1B5E3B;padding:28px 24px;">
                <p style="margin:0;font-family:Arial,sans-serif;font-size:26px;color:#FFFFFF;font-weight:700;">Five Rules</p>
              </td>
            </tr>
            <tr><td style="padding:36px 32px;">
              <p style="margin:0 0 8px 0;font-family:Arial,sans-serif;font-size:22px;color:#171D19;text-align:center;font-weight:700;">$heading</p>
              <p style="margin:0 0 24px 0;font-family:Arial,sans-serif;font-size:15px;color:#516358;text-align:center;">$intro</p>
              <p style="margin:0 0 16px 0;font-family:'Courier New',monospace;font-size:36px;color:#1B5E3B;text-align:center;letter-spacing:8px;font-weight:700;">$code</p>
              <p style="margin:0;font-family:Arial,sans-serif;font-size:13px;color:#6F737A;text-align:center;">Код действует 10 минут.</p>
            </td></tr>
          </table>
        </td></tr>
      </table>
    </body>
    </html>
""".trimIndent()

@Serializable
private data class ResendEmailRequest(
    @SerialName("from") val from: String,
    val to: List<String>,
    val subject: String,
    val html: String,
    val text: String,
)
