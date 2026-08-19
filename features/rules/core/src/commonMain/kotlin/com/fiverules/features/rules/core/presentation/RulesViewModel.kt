package com.fiverules.features.rules.core.presentation

import com.fiverules.common.core.MviViewModel
import com.fiverules.common.core.UiAction
import com.fiverules.common.core.UiState
import com.fiverules.common.navigation.AppNavigator
import com.fiverules.features.home.api.HomeRoute
import com.fiverules.features.profile.api.ProfileRoute

enum class TaskCardStatus { Highlighted, Default, Completed, Locked }

data class TaskCardUi(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskCardStatus,
)

data class TaskSectionUi(
    val title: String,
    val tasks: List<TaskCardUi>,
)

data class RulesUiState(
    val sections: List<TaskSectionUi> = emptyList(),
) : UiState

sealed interface RulesUiAction : UiAction {
    data object OpenHome : RulesUiAction
    data object OpenLessons : RulesUiAction
    data object OpenProfile : RulesUiAction
    data class OpenTask(val id: String) : RulesUiAction
}

class RulesViewModel(
    private val navigator: AppNavigator,
) : MviViewModel<RulesUiState, RulesUiAction>() {
    override fun initState(): RulesUiState = RulesUiState(
        sections = MockTaskSections,
    )

    override fun onAction(action: RulesUiAction) {
        when (action) {
            RulesUiAction.OpenHome -> navigator.navigateTab(HomeRoute, HomeRoute)
            RulesUiAction.OpenProfile -> navigator.navigateTab(ProfileRoute, HomeRoute)
            RulesUiAction.OpenLessons,
            is RulesUiAction.OpenTask,
            -> Unit
        }
    }
}

private val MockTaskSections = listOf(
    TaskSectionUi(
        title = "Today",
        tasks = listOf(
            TaskCardUi(
                id = "today-1",
                title = "Don't complain",
                description = "Catch every complaint and turn it into gratitude.",
                status = TaskCardStatus.Highlighted,
            ),
        ),
    ),
    TaskSectionUi(
        title = "Tasks",
        tasks = listOf(
            TaskCardUi(
                id = "task-2",
                title = "Don't gossip",
                description = "Speak only of people as if they were in the room.",
                status = TaskCardStatus.Completed,
            ),
            TaskCardUi(
                id = "task-3",
                title = "Be on time",
                description = "Available after you complete today's task.",
                status = TaskCardStatus.Locked,
            ),
            TaskCardUi(
                id = "task-4",
                title = "Keep your word",
                description = "Available on the next level.",
                status = TaskCardStatus.Locked,
            ),
        ),
    ),
)
