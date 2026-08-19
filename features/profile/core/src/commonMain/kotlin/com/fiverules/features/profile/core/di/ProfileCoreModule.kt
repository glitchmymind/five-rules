package com.fiverules.features.profile.core.di

import com.fiverules.features.profile.core.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileCoreModule = module {
    viewModelOf(::ProfileViewModel)
}
