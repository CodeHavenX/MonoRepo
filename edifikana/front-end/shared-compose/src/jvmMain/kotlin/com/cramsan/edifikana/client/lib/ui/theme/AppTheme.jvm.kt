package com.cramsan.edifikana.client.lib.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    darkColorScheme: ColorScheme,
    lightColorScheme: ColorScheme,
): ColorScheme {
    return when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
}

@Composable
actual fun WindowDecorations(
    colorScheme: ColorScheme,
    darkTheme: Boolean,
) = Unit
