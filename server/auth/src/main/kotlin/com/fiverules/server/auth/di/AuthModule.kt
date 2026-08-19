package com.fiverules.server.auth.di

import com.fiverules.server.auth.AuthRepository
import com.fiverules.server.auth.EmailService
import com.fiverules.server.auth.EmailVerificationService
import com.fiverules.server.auth.PasswordResetService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authModule = module {
    singleOf(::EmailService)
    singleOf(::EmailVerificationService)
    singleOf(::PasswordResetService)
    singleOf(::AuthRepository)
}
