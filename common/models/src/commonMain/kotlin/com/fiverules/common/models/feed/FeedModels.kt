package com.fiverules.common.models.feed

import com.fiverules.common.models.rules.RuleDto
import com.fiverules.common.models.user.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class FeedDto(
    val id: String,
    val text: String,
    val createdAt: String,
    val user: UserResponse,
    val rule: RuleDto,
)

@Serializable
data class CreateFeedRequest(
    val text: String,
    val ruleId: String,
)
