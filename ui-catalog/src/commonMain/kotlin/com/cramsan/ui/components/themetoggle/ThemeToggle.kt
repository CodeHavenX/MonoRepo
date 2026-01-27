package com.cramsan.ui.components.themetoggle

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.DarkMode
import androidx.compose.material.icons.sharp.LightMode
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A [ThemeToggle] which lets you toggle between light theme, dark theme and automatic selection.
 *
 * @author Cramsan
 */
@Composable
fun ThemeToggle(
    selectedTheme: SelectedTheme,
    onThemeSelected: (SelectedTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {
            val nextTheme = when (selectedTheme) {
                SelectedTheme.LIGHT -> SelectedTheme.DARK
                SelectedTheme.DARK -> SelectedTheme.SYSTEM_DEFAULT
                SelectedTheme.SYSTEM_DEFAULT -> SelectedTheme.LIGHT
            }
            onThemeSelected(nextTheme)
        },
        modifier = modifier,
    ) {
        AnimatedContent(targetState = selectedTheme) { theme ->
            when (theme) {
                SelectedTheme.LIGHT -> Icon(
                    imageVector = Icons.Sharp.LightMode,
                    contentDescription = "Light Theme",
                )

                SelectedTheme.DARK -> Icon(
                    imageVector = Icons.Sharp.DarkMode,
                    contentDescription = "Dark Theme",
                )

                SelectedTheme.SYSTEM_DEFAULT -> Icon(
                    imageVector = Icons.Sharp.Settings,
                    contentDescription = "System Default Theme",
                )
            }
        }
    }
}

/**
 * The selected theme enum.
 */
enum class SelectedTheme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT,
}
