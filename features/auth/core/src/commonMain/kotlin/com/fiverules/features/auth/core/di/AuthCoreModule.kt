package com.fiverules.features.auth.core.di

import com.fiverules.common.network.HttpClientNames
import com.fiverules.features.auth.core.data.AuthApi
import com.fiverules.features.auth.core.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authCoreModule = module {
    single { AuthApi(httpClient = get(named(HttpClientNames.PUBLIC))) }
    viewModelOf(::LoginViewModel)
}
