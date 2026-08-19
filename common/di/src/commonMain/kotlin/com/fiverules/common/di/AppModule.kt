package com.fiverules.common.di

import com.fiverules.common.core.AppEnvironment
import com.fiverules.common.core.apiBaseUrl
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.common.navigation.NavigationFeatureRegistry
import com.fiverules.common.network.HttpClientNames
import com.fiverules.common.network.TokenRepository
import com.fiverules.common.network.createAuthedHttpClient
import com.fiverules.common.network.createHttpClient
import com.fiverules.common.network.localLoopbackHost
import com.fiverules.features.auth.core.di.authCoreModule
import com.fiverules.features.auth.ui.di.authUiModule
import com.fiverules.features.feed.core.di.feedCoreModule
import com.fiverules.features.feed.ui.di.feedUiModule
import com.fiverules.features.home.core.di.homeCoreModule
import com.fiverules.features.home.ui.di.homeUiModule
import com.fiverules.features.rules.core.di.rulesCoreModule
import com.fiverules.features.rules.ui.di.rulesUiModule
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { Settings() }
    single(named(HttpClientNames.PUBLIC)) {
        val environment = get<AppEnvironment>()
        createHttpClient(environment.apiBaseUrl(localLoopbackHost()))
    }
    single {
        TokenRepository(
            settings = get(),
            publicClient = get(named(HttpClientNames.PUBLIC)),
        )
    }
    single(named(HttpClientNames.AUTHED)) {
        val environment = get<AppEnvironment>()
        createAuthedHttpClient(
            baseUrl = environment.apiBaseUrl(localLoopbackHost()),
            tokenRepository = get(),
        )
    }
}

val navigationModule = module {
    single { AppNavigator() }
    single { NavigationFeatureRegistry(navigationFeatures = getAll<NavigationFeature>()) }
}

val featureModules: List<Module> = authCoreModule + authUiModule +
    homeCoreModule + homeUiModule +
    rulesCoreModule + rulesUiModule +
    feedCoreModule + feedUiModule

fun initKoin(
    environment: AppEnvironment = AppEnvironment.current,
    appDeclaration: KoinAppDeclaration = {},
) {
    startKoin {
        appDeclaration()
        modules(
            listOf(
                module { single { environment } },
                networkModule,
                navigationModule,
            ) + featureModules
        )
    }
}
