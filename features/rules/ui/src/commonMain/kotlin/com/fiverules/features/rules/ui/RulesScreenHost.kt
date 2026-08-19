package com.fiverules.features.rules.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.common.uikit.components.FrMainTab
import com.fiverules.common.uikit.components.FrMainToolbar
import com.fiverules.common.uikit.components.FrScreenBackground
import com.fiverules.common.uikit.components.FrScreenHeader
import com.fiverules.common.uikit.components.FrTaskCard
import com.fiverules.common.uikit.components.FrTaskCardStyle
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing
import com.fiverules.features.rules.core.presentation.RulesUiAction
import com.fiverules.features.rules.core.presentation.RulesUiState
import com.fiverules.features.rules.core.presentation.RulesViewModel
import com.fiverules.features.rules.core.presentation.TaskCardStatus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RulesScreenHost(
    viewModel: RulesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskListScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun TaskListScreen(
    state: RulesUiState,
    onAction: (RulesUiAction) -> Unit,
) {
    val colors = FrTheme.colors
    val typography = FrTheme.typography

    FrScreenBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                FrScreenHeader()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Spacing.sm,
                        end = Spacing.sm,
                        top = Spacing.xl,
                        bottom = FrSize.toolbar + Spacing.lg + Spacing.xxl,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xl),
                ) {
                    items(state.sections, key = { it.title }) { section ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Text(
                                text = section.title,
                                style = typography.normalMedium,
                                color = colors.textPrimary,
                            )
                            section.tasks.forEach { task ->
                                FrTaskCard(
                                    title = task.title,
                                    description = task.description,
                                    style = task.status.toCardStyle(),
                                    onClick = { onAction(RulesUiAction.OpenTask(task.id)) },
                                )
                            }
                        }
                    }
                }
            }
            FrMainToolbar(
                selected = FrMainTab.Tasks,
                onTabClick = { tab ->
                    when (tab) {
                        FrMainTab.Home -> onAction(RulesUiAction.OpenHome)
                        FrMainTab.Tasks -> Unit
                        FrMainTab.Lessons -> onAction(RulesUiAction.OpenLessons)
                        FrMainTab.Profile -> onAction(RulesUiAction.OpenProfile)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = Spacing.lg),
            )
        }
    }
}

private fun TaskCardStatus.toCardStyle(): FrTaskCardStyle = when (this) {
    TaskCardStatus.Highlighted -> FrTaskCardStyle.Highlighted
    TaskCardStatus.Default -> FrTaskCardStyle.Default
    TaskCardStatus.Completed -> FrTaskCardStyle.Completed
    TaskCardStatus.Locked -> FrTaskCardStyle.Locked
}
