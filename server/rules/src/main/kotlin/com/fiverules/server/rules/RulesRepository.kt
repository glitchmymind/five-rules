package com.fiverules.server.rules

import com.fiverules.common.models.rules.CreateRuleRequest
import com.fiverules.common.models.rules.CreateTaskRequest
import com.fiverules.common.models.rules.RuleDto
import com.fiverules.common.models.rules.TaskDto
import com.fiverules.server.db.tables.RuleTasksTable
import com.fiverules.server.db.tables.RulesTable
import com.fiverules.server.db.tables.TasksTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class RulesRepository(
    private val db: Database,
) {
    suspend fun createRule(request: CreateRuleRequest): RuleDto =
        newSuspendedTransaction(Dispatchers.IO, db) {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val id = RulesTable.insertAndGetId {
                it[name] = request.name
                it[description] = request.description
                it[createdAt] = now
            }
            RuleDto(id = id.value.toString(), name = request.name, description = request.description)
        }

    suspend fun deleteRule(ruleId: UUID): Boolean =
        newSuspendedTransaction(Dispatchers.IO, db) {
            RuleTasksTable.deleteWhere { RuleTasksTable.ruleId eq ruleId }
            RulesTable.deleteWhere { RulesTable.id eq ruleId } > 0
        }

    suspend fun createTask(request: CreateTaskRequest): TaskDto =
        newSuspendedTransaction(Dispatchers.IO, db) {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val id = TasksTable.insertAndGetId {
                it[name] = request.name
                it[description] = request.description
                it[createdAt] = now
            }
            TaskDto(id = id.value.toString(), name = request.name, description = request.description)
        }

    suspend fun deleteTask(taskId: UUID): Boolean =
        newSuspendedTransaction(Dispatchers.IO, db) {
            RuleTasksTable.deleteWhere { RuleTasksTable.taskId eq taskId }
            TasksTable.deleteWhere { TasksTable.id eq taskId } > 0
        }

    suspend fun attachTasks(ruleId: UUID, taskIds: List<UUID>) {
        newSuspendedTransaction(Dispatchers.IO, db) {
            taskIds.forEach { taskId ->
                val exists = RuleTasksTable.selectAll()
                    .where { (RuleTasksTable.ruleId eq ruleId) and (RuleTasksTable.taskId eq taskId) }
                    .empty()
                    .not()
                if (!exists) {
                    RuleTasksTable.insert {
                        it[RuleTasksTable.ruleId] = ruleId
                        it[RuleTasksTable.taskId] = taskId
                    }
                }
            }
        }
    }

    suspend fun fetchRules(): List<RuleDto> =
        newSuspendedTransaction(Dispatchers.IO, db) {
            RulesTable.selectAll().map { row ->
                val ruleId = row[RulesTable.id].value
                RuleDto(
                    id = ruleId.toString(),
                    name = row[RulesTable.name],
                    description = row[RulesTable.description],
                    taskList = tasksForRule(ruleId),
                )
            }
        }

    suspend fun fetchTasks(): List<TaskDto> =
        newSuspendedTransaction(Dispatchers.IO, db) {
            TasksTable.selectAll().map { row ->
                TaskDto(
                    id = row[TasksTable.id].value.toString(),
                    name = row[TasksTable.name],
                    description = row[TasksTable.description],
                )
            }
        }

    suspend fun getRule(ruleId: UUID): RuleDto? =
        newSuspendedTransaction(Dispatchers.IO, db) {
            val row = RulesTable.selectAll()
                .where { RulesTable.id eq ruleId }
                .singleOrNull()
                ?: return@newSuspendedTransaction null
            RuleDto(
                id = ruleId.toString(),
                name = row[RulesTable.name],
                description = row[RulesTable.description],
                taskList = tasksForRule(ruleId),
            )
        }

    private fun tasksForRule(ruleId: UUID): List<TaskDto> {
        val join = RuleTasksTable.innerJoin(TasksTable)
        return join.selectAll()
            .where { RuleTasksTable.ruleId eq ruleId }
            .map { row ->
                TaskDto(
                    id = row[TasksTable.id].value.toString(),
                    name = row[TasksTable.name],
                    description = row[TasksTable.description],
                )
            }
    }
}
