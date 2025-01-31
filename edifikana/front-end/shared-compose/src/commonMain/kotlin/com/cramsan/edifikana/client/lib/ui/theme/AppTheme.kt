package com.cramsan.edifikana.client.lib.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.util.DebugLogger
import com.cramsan.edifikana.client.lib.ui.di.Coil3Provider

@Suppress("MagicNumber")
private val md_theme_light_primary = Color(0xFF13BC9A)

private val LightThemeColors = lightColorScheme(
    primary = md_theme_light_primary,
)
private val DarkThemeColors = lightColorScheme(
    primary = md_theme_light_primary,
)

/**
 * App theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    coil3Provider: Coil3Provider? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme, dynamicColor, DarkThemeColors, LightThemeColors)

    WindowDecorations(colorScheme, darkTheme)

    coil3Provider?.coil3Integration?.let {
        setSingletonImageLoaderFactory { platformContext ->
            ImageLoader.Builder(platformContext)
                .components {
                    add(it)
                    add(KtorNetworkFetcherFactory())
                }
                .logger(DebugLogger())
                .build()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
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
