package com.fiverules.common.network

import com.fiverules.common.models.ApiPaths
import com.fiverules.common.models.auth.AuthTokensResponse
import com.fiverules.common.models.auth.RefreshRequest
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenRepository(
    private val settings: Settings,
    private val publicClient: HttpClient,
) {
    private val _isAuthorized = MutableStateFlow(getAccessToken() != null)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    fun getAccessToken(): String? = settings.getStringOrNull(TokenKeys.ACCESS)
    fun getRefreshToken(): String? = settings.getStringOrNull(TokenKeys.REFRESH)

    fun bearerTokens(): BearerTokens? {
        val access = getAccessToken() ?: return null
        val refresh = getRefreshToken() ?: return null
        return BearerTokens(access, refresh)
    }

    fun saveTokens(access: String, refresh: String) {
        settings.putString(TokenKeys.ACCESS, access)
        settings.putString(TokenKeys.REFRESH, refresh)
        _isAuthorized.value = true
    }

    fun clear() {
        settings.remove(TokenKeys.ACCESS)
        settings.remove(TokenKeys.REFRESH)
        _isAuthorized.value = false
    }

    suspend fun refresh(): BearerTokens? {
        val refreshToken = getRefreshToken() ?: return null
        return try {
            val tokens = publicClient.post(ApiPaths.AUTH_REFRESH) {
                setBody(RefreshRequest(refreshToken))
            }.body<AuthTokensResponse>()
            saveTokens(tokens.accessToken, tokens.refreshToken)
            BearerTokens(tokens.accessToken, tokens.refreshToken)
        } catch (_: Exception) {
            clear()
            null
        }
    }
}
