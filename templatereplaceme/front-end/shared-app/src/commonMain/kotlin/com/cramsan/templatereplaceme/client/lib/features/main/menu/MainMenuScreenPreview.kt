package com.cramsan.templatereplaceme.client.lib.features.main.menu

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun MainMenuScreenPreview() =
    AppTheme {
        MainMenuContent(
            uiState = MainMenuUIState(isLoading = false),
        )
    }
