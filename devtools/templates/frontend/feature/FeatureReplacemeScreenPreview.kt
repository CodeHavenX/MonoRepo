package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Compose preview for [FeatureReplacemeContent].
 *
 * Add more preview functions below to cover different states (loading, error, populated).
 *
 * Example:
 * ```
 * @DevicePreviews
 * @Composable
 * private fun FeatureReplacemeScreenLoadingPreview() =
 *     AppTheme { FeatureReplacemeContent(uiState = FeatureReplacemeUIState(isLoading = true)) }
 * ```
 * We have our own wrappers to generate Previews, so we discourage the usage of the native @Preview annotations. Instead
 * we recommend using the following:
 *  - @ScreenPreviews: It is used for generating a preview(s) for a complete screen.
 *  - @ComponentPreviews: It is used for generating a preview(s) for a sub-component such as a button, list item, etc.
 *  - @DevicePreviews: It is used for generating a set of previews to represent multiple device form factors.
 *
 *  For the primary preview we will use @DevicePreviews
 */
@DevicePreviews
@Composable
private fun FeatureReplacemeScreenPreview() =
    AppTheme {
        FeatureReplacemeContent(
            uiState = FeatureReplacemeUIState(isLoading = false),
        )
    }


/**
 *  For a variant preview we will use @ScreenPreviews
 */
@ScreenPreviews
@Composable
private fun FeatureReplacemeScreenPreview_Loading() =
    AppTheme {
        FeatureReplacemeContent(
            uiState = FeatureReplacemeUIState(isLoading = true),
        )
    }
