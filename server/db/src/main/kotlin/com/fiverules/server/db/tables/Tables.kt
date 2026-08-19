package com.fiverules.server.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object UsersTable : UUIDTable("users") {
    val email = text("email").uniqueIndex()
    val passwordHash = text("password_hash").nullable()
    val displayName = text("display_name").nullable()
    val avatarUrl = text("avatar_url").nullable()
    val rating = integer("rating").default(1)
    val isBlocked = bool("is_blocked").default(false)
    val emailVerified = bool("email_verified").default(false)
    val emailVerifiedAt = timestampWithTimeZone("email_verified_at").nullable()
    val createdAt = timestampWithTimeZone("created_at")
}

object EmailVerificationCodesTable : Table("email_verification_codes") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val codeHash = text("code_hash")
    val expiresAt = timestampWithTimeZone("expires_at")
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(userId)
}

object PasswordResetCodesTable : Table("password_reset_codes") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val codeHash = text("code_hash")
    val expiresAt = timestampWithTimeZone("expires_at")
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(userId)
}

object RulesTable : UUIDTable("rules") {
    val name = varchar("name", 100)
    val description = varchar("description", 300)
    val createdAt = timestampWithTimeZone("created_at")
}

object TasksTable : UUIDTable("tasks") {
    val name = varchar("name", 100)
    val description = varchar("description", 300)
    val createdAt = timestampWithTimeZone("created_at")
}

object RuleTasksTable : Table("rule_tasks") {
    val ruleId = reference("rule_id", RulesTable, onDelete = ReferenceOption.CASCADE)
    val taskId = reference("task_id", TasksTable, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(ruleId, taskId)
}

object FeedsTable : UUIDTable("feeds") {
    val text = varchar("text", 300)
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val ruleId = reference("rule_id", RulesTable, onDelete = ReferenceOption.RESTRICT)
    val createdAt = timestampWithTimeZone("created_at")
}

object MessagesTable : UUIDTable("messages") {
    val feedId = reference("feed_id", FeedsTable, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val text = varchar("text", 300)
    val createdAt = timestampWithTimeZone("created_at")
}
