package com.cramsan.edifikana.client.lib.features.splash

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Splash feature screen.
 */
@ScreenPreviews
@Composable
private fun SplashScreenPreview() {
    SplashContent(
        content =
        SplashUIState(
            isLoading = true,
        ),
    )
}
