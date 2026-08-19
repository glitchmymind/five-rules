package com.fiverules.common.models

object ApiPaths {
    const val V1 = "api/v1"
    const val HEALTH = "$V1/health"
    const val AUTH_LOGIN = "$V1/auth/login"
    const val AUTH_REGISTER = "$V1/auth/register"
    const val AUTH_VERIFY_EMAIL = "$V1/auth/verify-email"
    const val AUTH_RESEND_VERIFICATION = "$V1/auth/resend-verification"
    const val AUTH_REFRESH = "$V1/auth/refresh"
    const val AUTH_FORGOT_PASSWORD = "$V1/auth/forgot-password"
    const val AUTH_RESET_PASSWORD = "$V1/auth/reset-password"
    const val RULES = "$V1/rules"
    const val TASKS = "$V1/tasks"
    const val FEEDS = "$V1/feeds"
}
