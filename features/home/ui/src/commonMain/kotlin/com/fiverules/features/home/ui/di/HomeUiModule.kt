package com.fiverules.features.home.ui.di

import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.home.ui.HomeNavigationFeature
import org.koin.dsl.bind
import org.koin.dsl.module

val homeUiModule = module {
    single { HomeNavigationFeature() } bind NavigationFeature::class
}
