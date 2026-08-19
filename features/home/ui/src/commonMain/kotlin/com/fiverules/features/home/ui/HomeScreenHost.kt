package com.fiverules.features.home.ui

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.common.models.feed.FeedDto
import com.fiverules.common.uikit.components.FrBanner
import com.fiverules.common.uikit.components.FrDailyRuleButton
import com.fiverules.common.uikit.components.FrPreviewMessage
import com.fiverules.common.uikit.components.FrMainTab
import com.fiverules.common.uikit.components.FrMainToolbar
import com.fiverules.common.uikit.components.FrScreenBackground
import com.fiverules.common.uikit.components.FrScreenHeader
import com.fiverules.common.uikit.theme.FrSize
import com.fiverules.common.uikit.theme.FrTheme
import com.fiverules.common.uikit.theme.Spacing
import com.fiverules.features.home.core.presentation.HomeUiAction
import com.fiverules.features.home.core.presentation.HomeUiState
import com.fiverules.features.home.core.presentation.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenHost(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
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
                        bottom = FrSize.toolbar + Spacing.lg + Spacing.xxl,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    item(key = "daily-rule") {
                        FrDailyRuleButton(
                            title = state.dailyRuleName?.takeIf { it.isNotBlank() } ?: "See you soon",
                            onClick = { onAction(HomeUiAction.OpenDailyRule) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    state.bannerTitle?.let { title ->
                        item(key = "banner") {
                            FrBanner(
                                title = title,
                                onClick = { onAction(HomeUiAction.OpenBanner) },
                                modifier = Modifier.padding(top = Spacing.md),
                            )
                        }
                    }
                    item(key = "community-title") {
                        Text(
                            text = "Community",
                            style = typography.normalMedium,
                            color = colors.textPrimary,
                            modifier = Modifier.padding(top = Spacing.sm),
                        )
                    }
                    if (state.isLoading && state.feeds.isEmpty()) {
                        item(key = "loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.xxl),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = colors.primary)
                            }
                        }
                    }
                    state.errorMessage?.let { error ->
                        item(key = "error") {
                            Text(
                                text = error,
                                style = typography.smallNormal,
                                color = colors.error,
                            )
                        }
                    }
                    items(state.feeds, key = { it.id }) { feed ->
                        FrPreviewMessage(
                            author = feed.authorName(),
                            ruleName = feed.rule.name,
                            text = feed.text,
                            time = formatFeedTime(feed.createdAt),
                        )
                    }
                }
            }
            FrMainToolbar(
                selected = FrMainTab.Home,
                onTabClick = { tab ->
                    when (tab) {
                        FrMainTab.Home -> onAction(HomeUiAction.Refresh)
                        FrMainTab.Tasks -> onAction(HomeUiAction.OpenTasks)
                        FrMainTab.Lessons -> onAction(HomeUiAction.OpenLessons)
                        FrMainTab.Profile -> onAction(HomeUiAction.OpenProfile)
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

private fun FeedDto.authorName(): String =
    user.displayName?.takeIf { it.isNotBlank() } ?: user.email.substringBefore("@")

private fun formatFeedTime(value: String): String {
    val trimmed = value.trim()
    if (trimmed.isEmpty() || trimmed.all(Char::isDigit)) return ""
    return trimmed
        .substringBefore('.')
        .replace('T', ' ')
        .take(16)
}
