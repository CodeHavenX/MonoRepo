package com.cramsan.sample.mpplib.compose.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Main view for the application.
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
