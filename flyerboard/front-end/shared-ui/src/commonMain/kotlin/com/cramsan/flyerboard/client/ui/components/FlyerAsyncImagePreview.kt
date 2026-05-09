package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FlyerAsyncImageWithUrlPreview() = AppTheme {
    FlyerAsyncImage(
        url = "https://example.com/flyer.jpg",
        contentDescription = "Sample flyer image",
    )
}

@Preview
@Composable
private fun FlyerAsyncImageNullUrlPreview() = AppTheme {
    FlyerAsyncImage(
        url = null,
        contentDescription = "No image available",
    )
}
