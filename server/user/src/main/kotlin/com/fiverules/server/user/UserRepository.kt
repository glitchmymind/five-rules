package com.fiverules.server.user

import com.fiverules.common.models.user.UserResponse
import com.fiverules.server.db.tables.UsersTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class UserRepository(
    private val db: Database,
) {
    suspend fun findByEmail(email: String): StoredUser? {
        val normalized = email.trim().lowercase()
        return newSuspendedTransaction(Dispatchers.IO, db) {
            UsersTable.selectAll()
                .where { UsersTable.email eq normalized }
                .singleOrNull()
                ?.toStoredUser()
        }
    }

    suspend fun findById(id: UUID): StoredUser? =
        newSuspendedTransaction(Dispatchers.IO, db) {
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .singleOrNull()
                ?.toStoredUser()
        }

    suspend fun create(
        email: String,
        password: String,
        displayName: String?,
    ): UserResponse {
        val normalizedEmail = email.trim().lowercase()
        return newSuspendedTransaction(Dispatchers.IO, db) {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val id = UsersTable.insertAndGetId {
                it[this.email] = normalizedEmail
                it[this.passwordHash] = PasswordHasher.hash(password)
                it[this.displayName] = displayName
                it[this.createdAt] = now
            }
            UserResponse(
                id = id.value.toString(),
                email = normalizedEmail,
                displayName = displayName,
                createdAt = Instant.from(now).toString(),
            )
        }
    }

    suspend fun verifyPassword(email: String, plainPassword: String): StoredUser? {
        val user = findByEmail(email) ?: return null
        val passwordHash = user.passwordHash ?: return null
        if (!PasswordHasher.verify(plainPassword, passwordHash)) return null
        return user
    }

    suspend fun markEmailVerified(id: UUID): StoredUser? =
        newSuspendedTransaction(Dispatchers.IO, db) {
            UsersTable.update({ UsersTable.id eq id }) {
                it[emailVerified] = true
                it[emailVerifiedAt] = OffsetDateTime.now(ZoneOffset.UTC)
            }
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .singleOrNull()
                ?.toStoredUser()
        }

    suspend fun updatePassword(id: UUID, newPassword: String): Boolean =
        newSuspendedTransaction(Dispatchers.IO, db) {
            UsersTable.update({ UsersTable.id eq id }) {
                it[passwordHash] = PasswordHasher.hash(newPassword)
            } > 0
        }

    suspend fun updateProfileForRegistration(
        id: UUID,
        password: String,
        displayName: String?,
    ): StoredUser? = newSuspendedTransaction(Dispatchers.IO, db) {
        UsersTable.update({ UsersTable.id eq id }) {
            it[passwordHash] = PasswordHasher.hash(password)
            if (displayName != null) {
                it[UsersTable.displayName] = displayName
            }
        }
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toStoredUser()
    }
}

fun StoredUser.toResponse(): UserResponse = UserResponse(
    id = id,
    email = email,
    displayName = displayName,
    avatarUrl = avatarUrl,
    rating = rating,
    isBlocked = isBlocked,
    emailVerified = emailVerified,
    createdAt = createdAtIso,
)

private fun ResultRow.toStoredUser(): StoredUser = StoredUser(
    id = this[UsersTable.id].value.toString(),
    email = this[UsersTable.email],
    passwordHash = this[UsersTable.passwordHash],
    displayName = this[UsersTable.displayName],
    avatarUrl = this[UsersTable.avatarUrl],
    rating = this[UsersTable.rating],
    isBlocked = this[UsersTable.isBlocked],
    emailVerified = this[UsersTable.emailVerified],
    createdAtIso = this[UsersTable.createdAt].toInstant().toString(),
)
