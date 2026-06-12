package com.cramsan.runasimi.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@Composable
@ComponentPreviews
private fun RunasimiTopBarPreview() =
    AppTheme {
        RunasimiTopBar(
            title = "Title",
            onNavigationIconSelected = {},
        )
    }
