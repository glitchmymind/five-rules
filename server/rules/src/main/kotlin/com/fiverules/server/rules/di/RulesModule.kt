package com.fiverules.server.rules.di

import com.fiverules.server.rules.RulesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val rulesModule = module {
    singleOf(::RulesRepository)
}
