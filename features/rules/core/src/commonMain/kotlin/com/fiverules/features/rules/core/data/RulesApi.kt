package com.fiverules.features.rules.core.data

import com.fiverules.common.models.ApiPaths
import com.fiverules.common.models.rules.CreateRuleRequest
import com.fiverules.common.models.rules.CreateTaskRequest
import com.fiverules.common.models.rules.RuleDto
import com.fiverules.common.models.rules.RulesResponse
import com.fiverules.common.models.rules.TaskDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class RulesApi(
    private val httpClient: HttpClient,
) {
    suspend fun getRules(): List<RuleDto> =
        httpClient.get(ApiPaths.RULES).body<RulesResponse>().rules

    suspend fun createRule(request: CreateRuleRequest): RuleDto =
        httpClient.post(ApiPaths.RULES) { setBody(request) }.body()

    suspend fun getTasks(): List<TaskDto> =
        httpClient.get(ApiPaths.TASKS).body()

    suspend fun createTask(request: CreateTaskRequest): TaskDto =
        httpClient.post(ApiPaths.TASKS) { setBody(request) }.body()
}
