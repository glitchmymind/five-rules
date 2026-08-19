package com.fiverules.features.rules.ui.di

import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.rules.ui.RulesNavigationFeature
import org.koin.dsl.bind
import org.koin.dsl.module

val rulesUiModule = module {
    single { RulesNavigationFeature() } bind NavigationFeature::class
}
