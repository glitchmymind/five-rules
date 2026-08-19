package com.fiverules.features.auth.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.auth.api.LoginRoute

class AuthNavigationFeature : NavigationFeature {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<LoginRoute> {
            LoginScreenHost()
        }
    }
}
