package com.cramsan.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun LoadingAnimationOverlayPreview_Loading() {
    LoadingAnimationOverlay(isLoading = true)
}

@Preview
@Composable
fun LoadingAnimationOverlayPreview_NotLoading() {
    LoadingAnimationOverlay(isLoading = false)
}
