package com.fiverules.features.profile.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fiverules.common.navigation.NavigationFeature
import com.fiverules.features.profile.api.ProfileRoute

class ProfileNavigationFeature : NavigationFeature {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable<ProfileRoute> {
            ProfileScreenHost()
        }
    }
}
