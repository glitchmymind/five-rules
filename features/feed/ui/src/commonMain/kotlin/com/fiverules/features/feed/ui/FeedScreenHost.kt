package com.fiverules.features.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.features.feed.core.presentation.FeedViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedScreenHost(
    viewModel: FeedViewModel = koinViewModel(),
) {
    viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize())
}
