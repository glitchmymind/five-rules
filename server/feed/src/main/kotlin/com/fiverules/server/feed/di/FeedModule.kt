package com.fiverules.server.feed.di

import com.fiverules.server.feed.FeedRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val feedModule = module {
    singleOf(::FeedRepository)
}
