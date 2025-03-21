package com.cramsan.edifikana.client.ui.components

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun EdifikanaTopBarPreview() = AppTheme {
    EdifikanaTopBar(
        title = "Title",
        onCloseClicked = {},
    )
}
