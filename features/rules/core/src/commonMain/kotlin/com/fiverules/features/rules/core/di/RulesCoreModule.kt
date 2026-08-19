package com.fiverules.features.rules.core.di

import com.fiverules.common.network.HttpClientNames
import com.fiverules.features.rules.core.data.RulesApi
import com.fiverules.features.rules.core.presentation.RulesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val rulesCoreModule = module {
    single { RulesApi(httpClient = get(named(HttpClientNames.AUTHED))) }
    viewModelOf(::RulesViewModel)
}
