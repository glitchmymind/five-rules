package com.fiverules.common.network

import com.fiverules.common.core.AppEnvironment
import com.fiverules.common.core.apiBaseUrl

object NetworkConfig {
    const val HEALTH_PATH = "/api/v1/health"
}

fun defaultBaseUrl(
    environment: AppEnvironment = AppEnvironment.current,
): String = environment.apiBaseUrl(localLoopbackHost())

expect fun localLoopbackHost(): String
