package com.cramsan.edifikana.client.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Get the color scheme based on the theme and dynamic color.
 */
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

/**
 * Apply window decorations based on the color scheme.
 */
@Composable
actual fun WindowDecorations(
    colorScheme: ColorScheme,
    darkTheme: Boolean,
) = Unit
