package com.fiverules.features.profile.core.presentation

import com.fiverules.common.core.MviViewModel
import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.features.home.api.HomeRoute
import com.fiverules.features.rules.api.RulesRoute

data class ProfileTaskUi(
    val id: String,
    val name: String,
)

data class ProfileRuleUi(
    val id: String,
    val name: String,
    val tasks: List<ProfileTaskUi>,
    val expanded: Boolean,
)

data class ProfileUiState(
    val userName: String = "",
    val currentLevel: Int = 1,
    val nextLevel: Int = 2,
    val progress: Float = 0f,
    val rules: List<ProfileRuleUi> = emptyList(),
) : UiState

sealed interface ProfileUiAction : UiAction {
    data object OpenHome : ProfileUiAction
    data object OpenTasks : ProfileUiAction
    data object OpenLessons : ProfileUiAction
    data object OpenSettings : ProfileUiAction
    data class ToggleRule(val id: String) : ProfileUiAction
}

class ProfileViewModel(
    private val navigator: AppNavigator,
) : MviViewModel<ProfileUiState, ProfileUiAction>() {
    override fun initState(): ProfileUiState = ProfileUiState(
        userName = "Alex",
        currentLevel = 1,
        nextLevel = 2,
        progress = 0.42f,
        rules = MockRules,
    )

    override fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.OpenHome -> navigator.navigateTab(HomeRoute, HomeRoute)
            ProfileUiAction.OpenTasks -> navigator.navigateTab(RulesRoute, HomeRoute)
            ProfileUiAction.OpenLessons,
            ProfileUiAction.OpenSettings,
            -> Unit
            is ProfileUiAction.ToggleRule -> updateState {
                copy(
                    rules = rules.map { rule ->
                        if (rule.id == action.id) rule.copy(expanded = !rule.expanded) else rule
                    },
                )
            }
        }
    }
}

private val MockRules = listOf(
    ProfileRuleUi(
        id = "rule-1",
        name = "Don't complain",
        expanded = true,
        tasks = listOf(
            ProfileTaskUi("t-1", "Notice a complaint"),
            ProfileTaskUi("t-2", "Replace it with gratitude"),
        ),
    ),
    ProfileRuleUi(
        id = "rule-2",
        name = "Don't gossip",
        expanded = false,
        tasks = listOf(
            ProfileTaskUi("t-3", "Speak as if they were here"),
        ),
    ),
    ProfileRuleUi(
        id = "rule-3",
        name = "Be on time",
        expanded = false,
        tasks = emptyList(),
    ),
    ProfileRuleUi(
        id = "rule-4",
        name = "Keep your word",
        expanded = false,
        tasks = emptyList(),
    ),
    ProfileRuleUi(
        id = "rule-5",
        name = "Do your best",
        expanded = false,
        tasks = emptyList(),
    ),
)
