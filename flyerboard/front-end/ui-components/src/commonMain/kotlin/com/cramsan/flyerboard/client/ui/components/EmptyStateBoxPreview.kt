package com.cramsan.flyerboard.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun EmptyStateBoxPreview() =
    AppTheme {
        EmptyStateBox(message = "No flyers found")
    }
