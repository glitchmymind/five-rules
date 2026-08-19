package com.fiverules.features.rules.core.presentation

import androidx.lifecycle.viewModelScope
import com.fiverules.common.core.MviViewModel
import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState
import com.fiverules.common.models.rules.RuleDto
import com.fiverules.common.models.rules.TaskDto
import com.fiverules.features.rules.core.data.RulesApi
import kotlinx.coroutines.launch

data class RulesUiState(
    val rules: List<RuleDto> = emptyList(),
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState

sealed interface RulesUiAction : UiAction {
    data object Refresh : RulesUiAction
}

class RulesViewModel(
    private val rulesApi: RulesApi,
) : MviViewModel<RulesUiState, RulesUiAction>() {
    override fun initState(): RulesUiState = RulesUiState()

    init {
        refresh()
    }

    override fun onAction(action: RulesUiAction) {
        when (action) {
            RulesUiAction.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                val rules = rulesApi.getRules()
                val tasks = rulesApi.getTasks()
                updateState {
                    copy(
                        rules = rules,
                        tasks = tasks,
                        isLoading = false,
                    )
                }
            } catch (_: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Не удалось загрузить правила") }
            }
        }
    }
}
