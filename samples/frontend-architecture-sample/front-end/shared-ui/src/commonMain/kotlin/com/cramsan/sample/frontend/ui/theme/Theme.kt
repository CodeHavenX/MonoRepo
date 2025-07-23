package com.cramsan.sample.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color constants
private const val TASK_BLUE_HEX = 0xFF1976D2
private const val TASK_BLUE_VARIANT_HEX = 0xFF0D47A1
private const val TASK_GREEN_HEX = 0xFF388E3C
private const val TASK_ORANGE_HEX = 0xFFF57C00
private const val TASK_RED_HEX = 0xFFD32F2F
private const val TASK_GRAY_HEX = 0xFF616161
private const val TASK_LIGHT_GRAY_HEX = 0xFFF5F5F5
private const val TASK_DARK_GRAY_HEX = 0xFF424242
private const val PRIMARY_CONTAINER_LIGHT_HEX = 0xFFE3F2FD
private const val SECONDARY_CONTAINER_LIGHT_HEX = 0xFFE8F5E8
private const val SECONDARY_CONTAINER_DARK_HEX = 0xFF1B5E20
private const val DARK_PRIMARY_HEX = 0xFF64B5F6
private const val DARK_SECONDARY_HEX = 0xFF81C784
private const val DARK_SECONDARY_CONTAINER_HEX = 0xFF2E7D32
private const val DARK_TERTIARY_HEX = 0xFFFFB74D
private const val DARK_ERROR_HEX = 0xFFEF5350
private const val DARK_BACKGROUND_HEX = 0xFF121212
private const val DARK_SURFACE_HEX = 0xFF1E1E1E
private const val DARK_SURFACE_VARIANT_HEX = 0xFFBDBDBD
private const val DARK_OUTLINE_HEX = 0xFF757575
private const val LOW_PRIORITY_HEX = 0xFF81C784
private const val MEDIUM_PRIORITY_HEX = 0xFFFFB74D
private const val HIGH_PRIORITY_HEX = 0xFFFF8A65
private const val URGENT_PRIORITY_HEX = 0xFFEF5350
private const val COMPLETED_TASK_HEX = 0xFF66BB6A
private const val OVERDUE_TASK_HEX = 0xFFEF5350

// Color palette for the task management theme
private val TaskBlue = Color(TASK_BLUE_HEX)
private val TaskBlueVariant = Color(TASK_BLUE_VARIANT_HEX)
private val TaskGreen = Color(TASK_GREEN_HEX)
private val TaskOrange = Color(TASK_ORANGE_HEX)
private val TaskRed = Color(TASK_RED_HEX)

private val TaskGray = Color(TASK_GRAY_HEX)
private val TaskLightGray = Color(TASK_LIGHT_GRAY_HEX)
private val TaskDarkGray = Color(TASK_DARK_GRAY_HEX)

private val LightColorScheme = lightColorScheme(
    primary = TaskBlue,
    onPrimary = Color.White,
    primaryContainer = Color(PRIMARY_CONTAINER_LIGHT_HEX),
    onPrimaryContainer = TaskBlueVariant,

    secondary = TaskGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(SECONDARY_CONTAINER_LIGHT_HEX),
    onSecondaryContainer = Color(SECONDARY_CONTAINER_DARK_HEX),

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
    primary = Color(DARK_PRIMARY_HEX),
    onPrimary = Color.Black,
    primaryContainer = TaskBlueVariant,
    onPrimaryContainer = Color(PRIMARY_CONTAINER_LIGHT_HEX),

    secondary = Color(DARK_SECONDARY_HEX),
    onSecondary = Color.Black,
    secondaryContainer = Color(DARK_SECONDARY_CONTAINER_HEX),
    onSecondaryContainer = Color(SECONDARY_CONTAINER_LIGHT_HEX),

    tertiary = Color(DARK_TERTIARY_HEX),
    onTertiary = Color.Black,

    error = Color(DARK_ERROR_HEX),
    onError = Color.Black,

    background = Color(DARK_BACKGROUND_HEX),
    onBackground = Color.White,

    surface = Color(DARK_SURFACE_HEX),
    onSurface = Color.White,
    surfaceVariant = TaskDarkGray,
    onSurfaceVariant = Color(DARK_SURFACE_VARIANT_HEX),

    outline = Color(DARK_OUTLINE_HEX)
)

/**
 * Main theme composable for the task management application.
 * Provides Material 3 theming with custom colors optimized for task management UI.
 *
 * @param darkTheme Whether to use the dark theme color scheme
 * @param content The composable content to be themed
 */
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

/**
 * Custom colors for task priorities and states.
 * Provides semantic colors for different task priorities and completion states.
 */
object TaskColors {
    val LowPriority = Color(LOW_PRIORITY_HEX)
    val MediumPriority = Color(MEDIUM_PRIORITY_HEX)
    val HighPriority = Color(HIGH_PRIORITY_HEX)
    val UrgentPriority = Color(URGENT_PRIORITY_HEX)

    val CompletedTask = Color(COMPLETED_TASK_HEX)
    val OverdueTask = Color(OVERDUE_TASK_HEX)
}
