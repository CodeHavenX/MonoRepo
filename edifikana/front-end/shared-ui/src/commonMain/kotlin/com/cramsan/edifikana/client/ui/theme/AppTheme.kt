package com.cramsan.edifikana.client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.util.DebugLogger
import com.cramsan.ui.components.LocalDebugLayoutInspection
import io.github.jan.supabase.coil.Coil3Integration

/**
 * App theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    coil3: Coil3Integration? = null,
    debugLayoutInspection: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme, dynamicColor, darkScheme, lightScheme)

    WindowDecorations(colorScheme, darkTheme)

    coil3?.let {
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

    CompositionLocalProvider(LocalDebugLayoutInspection provides debugLayoutInspection) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
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

const val LARGE_SCREEN_BREAK = 600
