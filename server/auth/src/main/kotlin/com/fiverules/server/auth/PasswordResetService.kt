package com.fiverules.server.auth

import com.fiverules.server.db.tables.PasswordResetCodesTable
import com.fiverules.server.db.tables.UsersTable
import com.fiverules.server.user.PasswordHasher
import com.fiverules.server.user.StoredUser
import com.fiverules.server.user.UserRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.security.SecureRandom
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class PasswordResetService(
    private val db: Database,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
) {
    private val random = SecureRandom()

    suspend fun requestReset(email: String): Boolean {
        val user = userRepository.findByEmail(email) ?: return true
        if (user.passwordHash == null) return true
        sendCode(user)
        return true
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Boolean {
        if (!code.matches(Regex("""\d{6}"""))) return false
        val user = userRepository.findByEmail(email) ?: return false
        if (user.passwordHash == null) return false
        val userId = UUID.fromString(user.id)
        val row = newSuspendedTransaction(Dispatchers.IO, db) {
            PasswordResetCodesTable.selectAll()
                .where { PasswordResetCodesTable.userId eq userId }
                .singleOrNull()
        } ?: return false
        if (row[PasswordResetCodesTable.expiresAt].isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            return false
        }
        if (!PasswordHasher.verify(code, row[PasswordResetCodesTable.codeHash])) return false
        if (!userRepository.updatePassword(userId, newPassword)) return false
        newSuspendedTransaction(Dispatchers.IO, db) {
            PasswordResetCodesTable.deleteWhere {
                PasswordResetCodesTable.userId eq userId
            }
        }
        return true
    }

    private suspend fun sendCode(user: StoredUser) {
        val code = random.nextInt(900_000).plus(100_000).toString()
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val userId = UUID.fromString(user.id)
        val stored = newSuspendedTransaction(Dispatchers.IO, db) {
            val previous = PasswordResetCodesTable.selectAll()
                .where { PasswordResetCodesTable.userId eq userId }
                .singleOrNull()
            if (
                previous != null &&
                previous[PasswordResetCodesTable.createdAt]
                    .isAfter(now.minusSeconds(RESEND_COOLDOWN_SECONDS))
            ) {
                return@newSuspendedTransaction false
            }
            PasswordResetCodesTable.deleteWhere {
                PasswordResetCodesTable.userId eq userId
            }
            PasswordResetCodesTable.insert {
                it[PasswordResetCodesTable.userId] = EntityID(userId, UsersTable)
                it[codeHash] = PasswordHasher.hash(code)
                it[expiresAt] = now.plusMinutes(CODE_TTL_MINUTES)
                it[createdAt] = now
            }
            true
        }
        if (stored) emailService.sendPasswordResetCode(user.email, code)
    }

    private companion object {
        const val CODE_TTL_MINUTES = 10L
        const val RESEND_COOLDOWN_SECONDS = 30L
    }
}
