package com.fiverules.server.feed

import com.fiverules.common.models.feed.CreateFeedRequest
import com.fiverules.common.models.feed.FeedDto
import com.fiverules.common.models.rules.RuleDto
import com.fiverules.server.db.tables.FeedsTable
import com.fiverules.server.rules.RulesRepository
import com.fiverules.server.user.UserRepository
import com.fiverules.server.user.toResponse
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class FeedRepository(
    private val db: Database,
    private val userRepository: UserRepository,
    private val rulesRepository: RulesRepository,
) {
    suspend fun create(userId: UUID, request: CreateFeedRequest): FeedDto? {
        val ruleId = runCatching { UUID.fromString(request.ruleId) }.getOrNull() ?: return null
        val user = userRepository.findById(userId) ?: return null
        val rule = rulesRepository.getRule(ruleId) ?: return null
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val id = newSuspendedTransaction(Dispatchers.IO, db) {
            FeedsTable.insertAndGetId {
                it[text] = request.text
                it[FeedsTable.userId] = userId
                it[FeedsTable.ruleId] = ruleId
                it[createdAt] = now
            }
        }
        return FeedDto(
            id = id.value.toString(),
            text = request.text,
            createdAt = Instant.from(now).toString(),
            user = user.toResponse(),
            rule = rule,
        )
    }

    suspend fun fetchAll(): List<FeedDto> {
        val rows = newSuspendedTransaction(Dispatchers.IO, db) {
            FeedsTable.selectAll().toList()
        }
        return rows.mapNotNull { row ->
            val user = userRepository.findById(row[FeedsTable.userId].value) ?: return@mapNotNull null
            val rule = rulesRepository.getRule(row[FeedsTable.ruleId].value)
                ?: RuleDto(id = row[FeedsTable.ruleId].value.toString(), name = "", description = "")
            FeedDto(
                id = row[FeedsTable.id].value.toString(),
                text = row[FeedsTable.text],
                createdAt = row[FeedsTable.createdAt].toInstant().toString(),
                user = user.toResponse(),
                rule = rule,
            )
        }
    }
}
