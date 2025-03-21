package com.codehavenx.alpaca.frontend.appcore.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Get the color scheme based on the theme. The capabilities for dynamic color settings are not currently
 * supported on WASM.
 */
@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    darkColorScheme: ColorScheme,
    lightColorScheme: ColorScheme
): ColorScheme {
    return when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
}

/**
 * Window decorations.
 */
@Composable
actual fun WindowDecorations(colorScheme: ColorScheme, darkTheme: Boolean) = Unit
