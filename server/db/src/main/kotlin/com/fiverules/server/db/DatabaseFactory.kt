package com.fiverules.server.db

import com.fiverules.server.core.AppConfig
import com.fiverules.server.db.tables.EmailVerificationCodesTable
import com.fiverules.server.db.tables.FeedsTable
import com.fiverules.server.db.tables.MessagesTable
import com.fiverules.server.db.tables.PasswordResetCodesTable
import com.fiverules.server.db.tables.RuleTasksTable
import com.fiverules.server.db.tables.RulesTable
import com.fiverules.server.db.tables.TasksTable
import com.fiverules.server.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun connect(): Database {
        logger.info("Connecting to PostgreSQL {} as {}", AppConfig.jdbcUrl, AppConfig.dbUser)
        return try {
            val hikari = HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = AppConfig.jdbcUrl
                    username = AppConfig.dbUser
                    password = AppConfig.dbPassword
                    driverClassName = "org.postgresql.Driver"
                    maximumPoolSize = 10
                    connectionTimeout = 5_000
                    isAutoCommit = false
                    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                    validate()
                },
            )
            Database.connect(hikari)
        } catch (error: Exception) {
            throw IllegalStateException(
                "Cannot connect to ${AppConfig.jdbcUrl} as ${AppConfig.dbUser}: ${error.message}",
                error,
            )
        }
    }

    fun createSchema(database: Database) {
        transaction(database) {
            SchemaUtils.create(
                UsersTable,
                EmailVerificationCodesTable,
                PasswordResetCodesTable,
                RulesTable,
                TasksTable,
                RuleTasksTable,
                FeedsTable,
                MessagesTable,
            )
            logger.info("PostgreSQL schema is ready")
        }
    }

    fun init(): Database? {
        return try {
            val database = connect()
            createSchema(database)
            database
        } catch (error: Exception) {
            logger.warn("Database is unavailable: ${error.message}")
            null
        }
    }
}
