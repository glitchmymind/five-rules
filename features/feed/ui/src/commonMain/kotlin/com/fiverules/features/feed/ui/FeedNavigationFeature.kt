package com.fiverules.features.feed.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.feed.api.FeedRoute

class FeedNavigationFeature : NavigationFeature {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<FeedRoute> {
            FeedScreenHost()
        }
    }
}
