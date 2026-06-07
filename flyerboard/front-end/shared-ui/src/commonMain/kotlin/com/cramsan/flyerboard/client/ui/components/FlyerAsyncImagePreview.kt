package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun FlyerAsyncImageWithUrlPreview() =
    AppTheme {
        FlyerAsyncImage(
            url = "https://example.com/flyer.jpg",
            contentDescription = "Sample flyer image",
        )
    }

@ComponentPreviews
@Composable
private fun FlyerAsyncImageNullUrlPreview() =
    AppTheme {
        FlyerAsyncImage(
            url = null,
            contentDescription = "No image available",
        )
    }
