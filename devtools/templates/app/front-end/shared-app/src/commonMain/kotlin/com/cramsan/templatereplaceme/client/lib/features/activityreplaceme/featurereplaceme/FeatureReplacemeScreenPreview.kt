package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FeatureReplacemeScreenPreview() =
    AppTheme {
        FeatureReplacemeContent(
            uiState = FeatureReplacemeUIState(isLoading = false),
        )
    }
