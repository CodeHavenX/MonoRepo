package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Compose preview for [FeatureReplacemeContent].
 *
 * Add more preview functions below to cover different states (loading, error, populated).
 *
 * Example:
 * ```
 * @Preview
 * @Composable
 * private fun FeatureReplacemeScreenLoadingPreview() =
 *     AppTheme { FeatureReplacemeContent(uiState = FeatureReplacemeUIState(isLoading = true)) }
 * ```
 */
@Preview
@Composable
private fun FeatureReplacemeScreenPreview() =
    AppTheme {
        FeatureReplacemeContent(
            uiState = FeatureReplacemeUIState(isLoading = false),
        )
    }
