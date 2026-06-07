package com.cramsan.sample.mpplib.compose.common

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Main view for the game.
 */
@Composable
fun MainView() {
    Content()
}

@ScreenPreviews
@Composable
private fun AppPreview() {
    Content()
}
