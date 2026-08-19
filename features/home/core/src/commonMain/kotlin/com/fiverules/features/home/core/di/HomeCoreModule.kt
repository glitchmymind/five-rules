package com.fiverules.features.home.core.di

import com.fiverules.features.home.core.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeCoreModule = module {
    viewModelOf(::HomeViewModel)
}
