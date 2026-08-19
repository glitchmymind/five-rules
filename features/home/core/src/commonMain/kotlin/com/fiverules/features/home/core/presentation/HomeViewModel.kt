package com.fiverules.features.home.core.presentation

import com.fiverules.common.core.MviViewModel
import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.common.network.TokenRepository
import com.fiverules.features.auth.api.LoginRoute
import com.fiverules.features.feed.api.FeedRoute
import com.fiverules.features.rules.api.RulesRoute

data object HomeUiState : UiState

sealed interface HomeUiAction : UiAction {
    data object OpenRules : HomeUiAction
    data object OpenFeed : HomeUiAction
    data object Logout : HomeUiAction
}

class HomeViewModel(
    private val navigator: AppNavigator,
    private val tokenRepository: TokenRepository,
) : MviViewModel<HomeUiState, HomeUiAction>() {
    override fun initState(): HomeUiState = HomeUiState

    override fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.OpenRules -> navigator.navigate(RulesRoute)
            HomeUiAction.OpenFeed -> navigator.navigate(FeedRoute)
            HomeUiAction.Logout -> {
                tokenRepository.clear()
                navigator.navigateAndClearStack(LoginRoute)
            }
        }
    }
}
