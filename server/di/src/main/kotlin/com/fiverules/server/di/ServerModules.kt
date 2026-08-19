package com.fiverules.server.di

import com.fiverules.server.auth.di.authModule
import com.fiverules.server.core.AppConfig
import com.fiverules.server.db.DatabaseFactory
import com.fiverules.server.feed.di.feedModule
import com.fiverules.server.rules.di.rulesModule
import com.fiverules.server.user.di.userModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val coreModule = module {
    single { AppConfig }
}

val dbModule = module {
    single<Database>(createdAtStart = true) {
        DatabaseFactory.connect().also { DatabaseFactory.createSchema(it) }
    }
}

val serverModules = listOf(
    coreModule,
    dbModule,
    userModule,
    authModule,
    rulesModule,
    feedModule,
)

fun Application.configureDi() {
    install(Koin) {
        slf4jLogger()
        modules(serverModules)
    }
}
