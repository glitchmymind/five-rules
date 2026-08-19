package com.fiverules

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.common.navigation.NavigationFeatureRegistry
import com.fiverules.common.network.TokenRepository
import com.fiverules.common.uikit.theme.FiveRulesTheme
import com.fiverules.features.auth.api.LoginRoute
import com.fiverules.features.home.api.HomeRoute
import org.koin.compose.koinInject

@Composable
fun App() {
    FiveRulesTheme {
        val tokenRepository = koinInject<TokenRepository>()
        val navigator = koinInject<AppNavigator>()
        val navigationFeatureRegistry = koinInject<NavigationFeatureRegistry>()
        val isAuthorized by tokenRepository.isAuthorized.collectAsStateWithLifecycle()
        val navController = rememberNavController()

        LaunchedEffect(navController) {
            navigator.controller = navController
        }

        NavHost(
            navController = navController,
            startDestination = if (isAuthorized) HomeRoute else LoginRoute,
        ) {
            navigationFeatureRegistry.registerAllGraphs(this)
        }
    }
}
