package com.fiverules.server.user.di

import com.fiverules.server.user.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val userModule = module {
    singleOf(::UserRepository)
}
