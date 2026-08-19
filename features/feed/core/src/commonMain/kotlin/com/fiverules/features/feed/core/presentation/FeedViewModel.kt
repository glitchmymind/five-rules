package com.fiverules.features.feed.core.presentation

import androidx.lifecycle.viewModelScope
import com.fiverules.common.core.MviViewModel
import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState
import com.fiverules.common.models.feed.FeedDto
import com.fiverules.features.feed.core.data.FeedApi
import kotlinx.coroutines.launch

data class FeedUiState(
    val feeds: List<FeedDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState

sealed interface FeedUiAction : UiAction {
    data object Refresh : FeedUiAction
}

class FeedViewModel(
    private val feedApi: FeedApi,
) : MviViewModel<FeedUiState, FeedUiAction>() {
    override fun initState(): FeedUiState = FeedUiState()

    init {
        refresh()
    }

    override fun onAction(action: FeedUiAction) {
        when (action) {
            FeedUiAction.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                val feeds = feedApi.getFeeds()
                updateState { copy(feeds = feeds, isLoading = false) }
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Не удалось загрузить ленту") }
            }
        }
    }
}
