package com.fiverules.features.auth.ui.di

import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.auth.ui.AuthNavigationFeature
import org.koin.dsl.bind
import org.koin.dsl.module

val authUiModule = module {
    single { AuthNavigationFeature() } bind NavigationFeature::class
}
