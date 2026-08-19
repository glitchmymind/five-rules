package com.fiverules.features.feed.ui.di

import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.feed.ui.FeedNavigationFeature
import org.koin.dsl.bind
import org.koin.dsl.module

val feedUiModule = module {
    single { FeedNavigationFeature() } bind NavigationFeature::class
}
