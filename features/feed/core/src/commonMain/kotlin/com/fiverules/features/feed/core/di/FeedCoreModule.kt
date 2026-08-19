package com.fiverules.features.feed.core.di

import com.fiverules.common.network.HttpClientNames
import com.fiverules.features.feed.core.data.FeedApi
import com.fiverules.features.feed.core.presentation.FeedViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val feedCoreModule = module {
    single { FeedApi(httpClient = get(named(HttpClientNames.AUTHED))) }
    viewModelOf(::FeedViewModel)
}
