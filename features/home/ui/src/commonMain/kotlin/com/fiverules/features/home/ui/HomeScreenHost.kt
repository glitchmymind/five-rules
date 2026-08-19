package com.fiverules.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fiverules.common.uikit.theme.Spacing
import com.fiverules.features.home.core.presentation.HomeUiAction
import com.fiverules.features.home.core.presentation.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenHost(
    viewModel: HomeViewModel = koinViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Button(onClick = { viewModel.onAction(HomeUiAction.OpenRules) }) { Text("Rules") }
        Button(onClick = { viewModel.onAction(HomeUiAction.OpenFeed) }) { Text("Feed") }
        TextButton(onClick = { viewModel.onAction(HomeUiAction.Logout) }) { Text("Выйти") }
    }
}
