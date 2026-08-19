package com.fiverules.features.profile.ui.di

import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.profile.ui.ProfileNavigationFeature
import org.koin.dsl.bind
import org.koin.dsl.module

val profileUiModule = module {
    single { ProfileNavigationFeature() } bind NavigationFeature::class
}
