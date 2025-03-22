package com.cramsan.framework.core.compose.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ThemeColorPreviewer() {
    ColorPreviewer()
}

@Preview
@Composable
private fun ThemeShapePreviewer() {
    ShapePreviewer()
}

@Preview
@Composable
private fun ThemeTypographyPreviewer() {
    TypographyPreviewer()
}
