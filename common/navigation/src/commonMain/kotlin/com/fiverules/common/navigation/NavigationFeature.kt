package com.fiverules.common.navigation

import androidx.navigation.NavGraphBuilder

interface NavigationFeature {
    fun registerGraph(navGraphBuilder: NavGraphBuilder)
}
