package com.cramsan.templatereplaceme.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.templatereplaceme.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun TemplateReplaceMeTopBarPreview() = AppTheme {
    TemplateReplaceMeTopBar(
        title = "Title",
        onNavigationIconSelected = {},
    )
}
