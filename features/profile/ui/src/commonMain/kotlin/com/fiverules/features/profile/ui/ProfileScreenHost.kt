package com.fiverules.features.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.common.uikit.components.FrActionButton
import com.fiverules.common.uikit.components.FrAvatar
import com.fiverules.common.uikit.components.FrListItem
import com.fiverules.common.uikit.components.FrListItemStyle
import com.fiverules.common.uikit.components.FrMainTab
import com.fiverules.common.uikit.components.FrMainToolbar
import com.fiverules.common.uikit.components.FrProgressBar
import com.fiverules.common.uikit.components.FrScreenBackground
import com.fiverules.common.uikit.icons.FrIcons
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing
import com.fiverules.features.profile.core.presentation.ProfileRuleUi
import com.fiverules.features.profile.core.presentation.ProfileUiAction
import com.fiverules.features.profile.core.presentation.ProfileUiState
import com.fiverules.features.profile.core.presentation.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreenHost(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    onAction: (ProfileUiAction) -> Unit,
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(FrSize.profileHead)
                        .padding(horizontal = Spacing.md),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    FrActionButton(
                        onClick = { onAction(ProfileUiAction.OpenSettings) },
                        icon = FrIcons.Placeholder,
                        contentDescription = "Settings",
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = FrSize.toolbar + Spacing.lg + Spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item(key = "user") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                        ) {
                            FrAvatar(
                                name = state.userName,
                                size = FrSize.avatarProfile,
                            )
                            Text(
                                text = state.userName,
                                style = typography.largeBold,
                                color = colors.textPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.md),
                            )
                        }
                    }
                    item(key = "progress") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = Spacing.md,
                                    end = Spacing.md,
                                    top = Spacing.lg,
                                ),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = state.currentLevel.toString(),
                                    style = typography.normalNormal,
                                    color = colors.primary,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "level",
                                    style = typography.normalNormal,
                                    color = colors.textMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = state.nextLevel.toString(),
                                    style = typography.normalNormal,
                                    color = colors.textMuted,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            FrProgressBar(progress = state.progress)
                        }
                    }
                    items(state.rules, key = { it.id }) { rule ->
                        ProfileRuleBlock(
                            rule = rule,
                            onToggle = { onAction(ProfileUiAction.ToggleRule(rule.id)) },
                            modifier = Modifier.padding(
                                start = Spacing.md,
                                end = Spacing.md,
                                top = Spacing.xl,
                            ),
                        )
                    }
                }
            }
            FrMainToolbar(
                selected = FrMainTab.Profile,
                onTabClick = { tab ->
                    when (tab) {
                        FrMainTab.Home -> onAction(ProfileUiAction.OpenHome)
                        FrMainTab.Tasks -> onAction(ProfileUiAction.OpenTasks)
                        FrMainTab.Lessons -> onAction(ProfileUiAction.OpenLessons)
                        FrMainTab.Profile -> Unit
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

@Composable
private fun ProfileRuleBlock(
    rule: ProfileRuleUi,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FrListItem(
            title = rule.name,
            style = FrListItemStyle.Rule,
            onClick = onToggle,
        )
        if (rule.expanded) {
            rule.tasks.forEach { task ->
                FrListItem(
                    title = task.name,
                    style = FrListItemStyle.Task,
                    onClick = null,
                )
            }
        }
    }
}
