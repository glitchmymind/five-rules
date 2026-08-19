package com.fiverules.common.navigation

import androidx.navigation.NavGraphBuilder

class NavigationFeatureRegistry(
    private val navigationFeatures: List<NavigationFeature>,
) {
    fun registerAllGraphs(navGraphBuilder: NavGraphBuilder) {
        navigationFeatures.forEach { feature ->
            feature.registerGraph(navGraphBuilder)
        }
    }
}
