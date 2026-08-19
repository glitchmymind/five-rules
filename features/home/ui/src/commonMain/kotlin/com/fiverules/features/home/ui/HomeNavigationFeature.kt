package com.fiverules.features.home.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.home.api.HomeRoute

class HomeNavigationFeature : NavigationFeature {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<HomeRoute> {
            HomeScreenHost()
        }
    }
}
