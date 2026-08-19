package com.fiverules.server.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

object JwtConfig {

    const val Jwt = "auth-jwt"
    const val userIdClaim = "userId"
    const val deviceIdClaim = "deviceId"
    private const val typeClaim = "type"
    private const val refreshTypeValue = "refresh"

    private val secret = AppConfig.jwtSecret
    private val issuer = AppConfig.jwtIssuer
    private val audience = AppConfig.jwtAudience
    private val accessValidity = 15.minutes.inWholeMilliseconds
    private val refreshValidity = 90.days.inWholeMilliseconds
    private val algorithm = Algorithm.HMAC256(secret)

    private val jwtVerifier by lazy {
        JWT.require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }

    val accessTokenTtlSeconds: Long
        get() = accessValidity / 1000L

    fun generateAccessToken(userId: String): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(userIdClaim, userId)
            .withExpiresAt(Date(System.currentTimeMillis() + accessValidity))
            .sign(algorithm)

    fun generateRefreshToken(userId: String, deviceId: String): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(userIdClaim, userId)
            .withClaim(deviceIdClaim, deviceId)
            .withClaim(typeClaim, refreshTypeValue)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshValidity))
            .sign(algorithm)

    fun configureKtorFeature(config: JWTAuthenticationProvider.Config) = with(config) {
        verifier(jwtVerifier)
        validate { credential ->
            val payload = credential.payload
            if (payload.getClaim(typeClaim).asString() == refreshTypeValue) null
            else if (payload.getClaim(userIdClaim).asString() != null) JWTPrincipal(payload)
            else null
        }
    }

    fun verifyRefreshToken(token: String): DecodedJWT? =
        try {
            val jwt = jwtVerifier.verify(token)
            if (jwt.getClaim(typeClaim).asString() == refreshTypeValue) jwt else null
        } catch (_: Exception) {
            null
        }
}
