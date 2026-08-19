package com.fiverules.common.navigation

import androidx.navigation.NavHostController

class AppNavigator {
    var controller: NavHostController? = null

    fun navigateAndClearStack(route: Any) {
        controller?.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navigate(route: Any) {
        controller?.navigate(route)
    }
}
