package com.cramsan.edifikana.client.ui.theme

import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.ui.ColorPreviewer
import com.cramsan.framework.core.compose.ui.ShapePreviewer
import com.cramsan.framework.core.compose.ui.TypographyPreviewer
import org.jetbrains.compose.ui.tooling.preview.Preview

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
