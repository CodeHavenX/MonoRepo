package com.cramsan.sample.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color palette for the task management theme
private val TaskBlue = Color(0xFF1976D2)
private val TaskBlueVariant = Color(0xFF0D47A1)
private val TaskGreen = Color(0xFF388E3C)
private val TaskOrange = Color(0xFFF57C00)
private val TaskRed = Color(0xFFD32F2F)

private val TaskGray = Color(0xFF616161)
private val TaskLightGray = Color(0xFFF5F5F5)
private val TaskDarkGray = Color(0xFF424242)

private val LightColorScheme = lightColorScheme(
    primary = TaskBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = TaskBlueVariant,

    secondary = TaskGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E8),
    onSecondaryContainer = Color(0xFF1B5E20),

    tertiary = TaskOrange,
    onTertiary = Color.White,

    error = TaskRed,
    onError = Color.White,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = TaskLightGray,
    onSurfaceVariant = TaskGray,

    outline = TaskGray
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color.Black,
    primaryContainer = TaskBlueVariant,
    onPrimaryContainer = Color(0xFFE3F2FD),

    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFE8F5E8),

    tertiary = Color(0xFFFFB74D),
    onTertiary = Color.Black,

    error = Color(0xFFEF5350),
    onError = Color.Black,

    background = Color(0xFF121212),
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = TaskDarkGray,
    onSurfaceVariant = Color(0xFFBDBDBD),

    outline = Color(0xFF757575)
)

@Composable
fun TaskManagementTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TaskTypography,
        content = content
    )
}

// Custom colors for task priorities
object TaskColors {
    val LowPriority = Color(0xFF81C784)
    val MediumPriority = Color(0xFFFFB74D)
    val HighPriority = Color(0xFFFF8A65)
    val UrgentPriority = Color(0xFFEF5350)

    val CompletedTask = Color(0xFF66BB6A)
    val OverdueTask = Color(0xFFEF5350)
}
