package com.cramsan.runasimi.client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.cramsan.ui.components.LocalDebugLayoutInspection

/**
 * App theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    debugLayoutInspection: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(darkTheme, dynamicColor, darkScheme, lightScheme)

    WindowDecorations(colorScheme, darkTheme)

    CompositionLocalProvider(LocalDebugLayoutInspection provides debugLayoutInspection) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

/**
 * Get color scheme.
 */
@Composable
expect fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    darkColorScheme: ColorScheme,
    lightColorScheme: ColorScheme,
): ColorScheme

/**
 * Window decorations.
 */
@Composable
expect fun WindowDecorations(
    colorScheme: ColorScheme,
    darkTheme: Boolean,
)
