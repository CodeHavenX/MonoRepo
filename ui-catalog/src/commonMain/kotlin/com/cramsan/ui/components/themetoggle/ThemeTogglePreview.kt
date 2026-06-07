package com.cramsan.ui.components.themetoggle

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews

@Composable
@ComponentPreviews
fun ThemeTogglePreviewLight() {
    ThemeToggle(selectedTheme = SelectedTheme.LIGHT, onThemeSelected = {})
}

@Composable
@ComponentPreviews
fun ThemeTogglePreviewDark() {
    ThemeToggle(selectedTheme = SelectedTheme.DARK, onThemeSelected = {})
}

@Composable
@ComponentPreviews
fun ThemeTogglePreviewSystemDefault() {
    ThemeToggle(selectedTheme = SelectedTheme.SYSTEM_DEFAULT, onThemeSelected = {})
}
