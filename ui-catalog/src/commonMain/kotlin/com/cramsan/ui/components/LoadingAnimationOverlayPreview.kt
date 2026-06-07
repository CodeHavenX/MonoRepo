package com.cramsan.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
fun LoadingAnimationOverlayPreview_Loading() {
    Box(
        modifier = Modifier.size(100.dp),
    ) {
        LoadingAnimationOverlay(isLoading = true)
    }
}

@ComponentPreviews
@Composable
fun LoadingAnimationOverlayPreview_NotLoading() {
    Box(
        modifier = Modifier.size(100.dp),
    ) {
        LoadingAnimationOverlay(isLoading = false)
    }
}
