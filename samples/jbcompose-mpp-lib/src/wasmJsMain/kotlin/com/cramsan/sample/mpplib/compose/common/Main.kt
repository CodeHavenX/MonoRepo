package com.cramsan.sample.mpplib.compose.common

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Main view for the application.
 */
@Composable
fun MainView() {
    Content()
}

/**
 * Preview for the main view.
 */
@ScreenPreviews
@Composable
fun AppPreview() {
    Content()
}
