package com.fiverules.features.rules.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.rules.api.RulesRoute

class RulesNavigationFeature : NavigationFeature {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<RulesRoute> {
            RulesScreenHost()
        }
    }
}
