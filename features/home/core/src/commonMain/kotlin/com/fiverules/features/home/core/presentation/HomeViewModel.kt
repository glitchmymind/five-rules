package com.fiverules.features.home.core.presentation

import androidx.lifecycle.viewModelScope
import com.fiverules.common.core.MviViewModel
import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState
import com.fiverules.common.models.feed.FeedDto
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.features.feed.core.data.FeedApi
import com.fiverules.features.home.api.HomeRoute
import com.fiverules.features.profile.api.ProfileRoute
import com.fiverules.features.rules.api.RulesRoute
import com.fiverules.features.rules.core.data.RulesApi
import kotlinx.coroutines.launch

data class HomeUiState(
    val dailyRuleName: String? = null,
    val bannerTitle: String? = null,
    val feeds: List<FeedDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState

sealed interface HomeUiAction : UiAction {
    data object Refresh : HomeUiAction
    data object OpenDailyRule : HomeUiAction
    data object OpenTasks : HomeUiAction
    data object OpenLessons : HomeUiAction
    data object OpenProfile : HomeUiAction
    data object OpenBanner : HomeUiAction
}

class HomeViewModel(
    private val navigator: AppNavigator,
    private val feedApi: FeedApi,
    private val rulesApi: RulesApi,
) : MviViewModel<HomeUiState, HomeUiAction>() {
    override fun initState(): HomeUiState = HomeUiState()

    init {
        refresh()
    }

    override fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.Refresh -> refresh()
            HomeUiAction.OpenDailyRule,
            HomeUiAction.OpenBanner,
            HomeUiAction.OpenTasks,
            -> navigator.navigateTab(RulesRoute, HomeRoute)
            HomeUiAction.OpenProfile -> navigator.navigateTab(ProfileRoute, HomeRoute)
            HomeUiAction.OpenLessons -> Unit
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                val rules = rulesApi.getRules()
                val dailyRule = rules.firstOrNull()
                val feeds = feedApi.getFeeds()
                updateState {
                    copy(
                        dailyRuleName = dailyRule?.name,
                        bannerTitle = dailyRule?.taskList?.firstOrNull()?.name,
                        feeds = feeds,
                        isLoading = false,
                    )
                }
            } catch (_: Exception) {
                updateState {
                    copy(isLoading = false, errorMessage = "Не удалось загрузить ленту")
                }
            }
        }
    }
}
