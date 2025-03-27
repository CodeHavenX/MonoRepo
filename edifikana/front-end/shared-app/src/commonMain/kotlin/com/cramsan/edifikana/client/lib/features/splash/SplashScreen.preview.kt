package com.cramsan.edifikana.client.lib.features.splash

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Splash feature screen.
 */
@Preview
@Composable
private fun SplashScreenPreview() {
    SplashContent(
        content = SplashUIState(
            isLoading = true,
        ),
    )
}
