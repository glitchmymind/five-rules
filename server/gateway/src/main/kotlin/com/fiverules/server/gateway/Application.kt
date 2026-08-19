package com.fiverules.server.gateway

import com.fiverules.server.auth.plugins.configureAuthentication
import com.fiverules.server.core.AppConfig
import com.fiverules.server.di.configureDi
import com.fiverules.server.network.configureNetwork
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(
        factory = Netty,
        port = AppConfig.port,
        host = AppConfig.host,
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    log.info("Database: {} as {}", AppConfig.jdbcUrl, AppConfig.dbUser)
    log.info(
        "Email: provider={} from={} resendKey={}",
        AppConfig.emailProvider,
        AppConfig.emailFrom,
        if (AppConfig.resendApiKey.isNullOrBlank()) "missing" else "loaded",
    )
    configureDi()
    configureAuthentication()
    configureNetwork()
    configureRouting()
}
