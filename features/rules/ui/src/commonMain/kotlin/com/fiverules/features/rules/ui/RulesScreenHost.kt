package com.fiverules.features.rules.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fiverules.features.rules.core.presentation.RulesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RulesScreenHost(
    viewModel: RulesViewModel = koinViewModel(),
) {
    viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize())
}
