package com.cramsan.framework.core.compose.ui

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun ThemeColorPreviewer() {
    ColorPreviewer()
}

@ComponentPreviews
@Composable
private fun ThemeShapePreviewer() {
    ShapePreviewer()
}

@ComponentPreviews
@Composable
private fun ThemeTypographyPreviewer() {
    TypographyPreviewer()
}
