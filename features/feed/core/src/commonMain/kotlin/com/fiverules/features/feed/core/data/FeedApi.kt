package com.fiverules.features.feed.core.data

import com.fiverules.common.models.ApiPaths
import com.fiverules.common.models.feed.CreateFeedRequest
import com.fiverules.common.models.feed.FeedDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class FeedApi(
    private val httpClient: HttpClient,
) {
    suspend fun getFeeds(): List<FeedDto> =
        httpClient.get(ApiPaths.FEEDS).body()

    suspend fun createFeed(request: CreateFeedRequest): FeedDto =
        httpClient.post(ApiPaths.FEEDS) { setBody(request) }.body()
}
