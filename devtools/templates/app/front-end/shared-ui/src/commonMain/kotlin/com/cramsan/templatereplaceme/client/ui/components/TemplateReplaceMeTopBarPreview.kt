package com.cramsan.templatereplaceme.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ComponentPreviews

@Composable
@ComponentPreviews
private fun TemplateReplaceMeTopBarPreview() =
    AppTheme {
        TemplateReplaceMeTopBar(
            title = "Title",
            onNavigationIconSelected = {},
        )
    }
