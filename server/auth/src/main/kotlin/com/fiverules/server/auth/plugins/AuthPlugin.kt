package com.fiverules.server.auth.plugins

import com.fiverules.server.core.JwtConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt(JwtConfig.Jwt) {
            JwtConfig.configureKtorFeature(this)
        }
    }
}
