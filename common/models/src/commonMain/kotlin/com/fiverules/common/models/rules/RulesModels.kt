package com.fiverules.common.models.rules

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val name: String,
    val description: String = "",
)

@Serializable
data class RuleDto(
    val id: String,
    val name: String,
    val description: String = "",
    val taskList: List<TaskDto> = emptyList(),
)

@Serializable
data class CreateRuleRequest(
    val name: String,
    val description: String = "",
)

@Serializable
data class CreateTaskRequest(
    val name: String,
    val description: String = "",
)

@Serializable
data class AttachTasksRequest(
    val taskIds: List<String>,
)

@Serializable
data class RulesResponse(
    val rules: List<RuleDto>,
)
