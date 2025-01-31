package com.cramsan.edifikana.client.ui.theme

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.ui.ColorPreviewer
import com.cramsan.framework.core.compose.ui.ShapePreviewer
import com.cramsan.framework.core.compose.ui.TypographyPreviewer

@Preview
@Composable
private fun ThemeColorPreviewer() {
    AppTheme {
        ColorPreviewer()
    }
}

@Preview
@Composable
private fun ThemeShapePreviewer() {
    AppTheme {
        ShapePreviewer()
    }
}

@Preview
@Composable
private fun ThemeTypographyPreviewer() {
    AppTheme {
        TypographyPreviewer()
    }
}
