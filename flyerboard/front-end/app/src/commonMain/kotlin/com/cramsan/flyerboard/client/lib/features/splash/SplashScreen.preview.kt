package com.cramsan.flyerboard.client.lib.features.splash

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.DevicePreviews

/**
 * Preview for the Splash feature screen.
 */
@DevicePreviews
@Composable
private fun SplashScreenPreview() {
    SplashContent(
        content = SplashUIState,
    )
}
