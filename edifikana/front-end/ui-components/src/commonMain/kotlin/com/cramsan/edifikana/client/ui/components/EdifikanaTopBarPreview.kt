package com.cramsan.edifikana.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@Composable
@ComponentPreviews
private fun EdifikanaTopBarPreview() =
    AppTheme {
        EdifikanaTopBar(
            title = "Title",
            onNavigationIconSelected = {},
        )
    }
