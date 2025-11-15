package com.cramsan.ui.components.themetoggle

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun ThemeTogglePreviewLight() {
    ThemeToggle(selectedTheme = SelectedTheme.LIGHT, onThemeSelected = {})
}

@Composable
@Preview
fun ThemeTogglePreviewDark() {
    ThemeToggle(selectedTheme = SelectedTheme.DARK, onThemeSelected = {})
}

@Composable
@Preview
fun ThemeTogglePreviewSystemDefault() {
    ThemeToggle(selectedTheme = SelectedTheme.SYSTEM_DEFAULT, onThemeSelected = {})
}
