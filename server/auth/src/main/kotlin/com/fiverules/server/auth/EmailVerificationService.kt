package com.fiverules.server.auth

import com.fiverules.server.db.tables.EmailVerificationCodesTable
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
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class EmailVerificationService(
    private val db: Database,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
) {
    private val logger = LoggerFactory.getLogger(EmailVerificationService::class.java)
    private val random = SecureRandom()

    suspend fun sendCode(user: StoredUser) {
        if (user.emailVerified) return
        val code = random.nextInt(900_000).plus(100_000).toString()
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val userId = UUID.fromString(user.id)
        val stored = newSuspendedTransaction(Dispatchers.IO, db) {
            val previous = EmailVerificationCodesTable.selectAll()
                .where { EmailVerificationCodesTable.userId eq userId }
                .singleOrNull()
            if (
                previous != null &&
                previous[EmailVerificationCodesTable.createdAt]
                    .isAfter(now.minusSeconds(RESEND_COOLDOWN_SECONDS))
            ) {
                return@newSuspendedTransaction false
            }
            EmailVerificationCodesTable.deleteWhere {
                EmailVerificationCodesTable.userId eq userId
            }
            EmailVerificationCodesTable.insert {
                it[EmailVerificationCodesTable.userId] = EntityID(userId, UsersTable)
                it[codeHash] = PasswordHasher.hash(code)
                it[expiresAt] = now.plusMinutes(CODE_TTL_MINUTES)
                it[createdAt] = now
            }
            true
        }
        if (stored) {
            emailService.sendVerificationCode(user.email, code)
        } else {
            logger.info("Skip verification email for {}: resend cooldown", user.email)
        }
    }

    suspend fun resend(email: String): Boolean {
        val user = userRepository.findByEmail(email) ?: return true
        if (!user.emailVerified) sendCode(user)
        return true
    }

    suspend fun verify(email: String, code: String): StoredUser? {
        if (!code.matches(Regex("""\d{6}"""))) return null
        val user = userRepository.findByEmail(email) ?: return null
        if (user.emailVerified) return user
        val userId = UUID.fromString(user.id)
        val row = newSuspendedTransaction(Dispatchers.IO, db) {
            EmailVerificationCodesTable.selectAll()
                .where { EmailVerificationCodesTable.userId eq userId }
                .singleOrNull()
        } ?: return null
        if (row[EmailVerificationCodesTable.expiresAt].isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            return null
        }
        if (!PasswordHasher.verify(code, row[EmailVerificationCodesTable.codeHash])) return null
        val verified = userRepository.markEmailVerified(userId) ?: return null
        newSuspendedTransaction(Dispatchers.IO, db) {
            EmailVerificationCodesTable.deleteWhere {
                EmailVerificationCodesTable.userId eq userId
            }
        }
        return verified
    }

    private companion object {
        const val CODE_TTL_MINUTES = 10L
        const val RESEND_COOLDOWN_SECONDS = 30L
    }
}
