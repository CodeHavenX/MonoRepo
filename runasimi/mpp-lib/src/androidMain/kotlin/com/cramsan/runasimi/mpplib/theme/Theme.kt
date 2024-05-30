package com.cramsan.runasimi.mpplib.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramsan.runasimi.mpplib.ui.theme.Dimension
import com.cramsan.runasimi.mpplib.ui.theme.RunasimiTheme
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_background
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_error
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_errorContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_inverseOnSurface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_inversePrimary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_inverseSurface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onBackground
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onError
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onErrorContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onPrimary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onPrimaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onSecondary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onSecondaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onSurface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onSurfaceVariant
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onTertiary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_onTertiaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_outline
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_outlineVariant
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_primary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_primaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_scrim
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_secondary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_secondaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_surface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_surfaceTint
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_surfaceVariant
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_tertiary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_dark_tertiaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_background
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_error
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_errorContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_inverseOnSurface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_inversePrimary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_inverseSurface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onBackground
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onError
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onErrorContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onPrimary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onPrimaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onSecondary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onSecondaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onSurface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onSurfaceVariant
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onTertiary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_onTertiaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_outline
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_outlineVariant
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_primary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_primaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_scrim
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_secondary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_secondaryContainer
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_surface
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_surfaceTint
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_surfaceVariant
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_tertiary
import com.cramsan.runasimi.mpplib.ui.theme.md_theme_light_tertiaryContainer

@Preview
@Composable
fun PreviewColors() {
    RunasimiTheme(false) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ColorPalette(
                title = "Light Mode",
                primary = md_theme_light_primary,
                onPrimary = md_theme_light_onPrimary,
                primaryContainer = md_theme_light_primaryContainer,
                onPrimaryContainer = md_theme_light_onPrimaryContainer,
                secondary = md_theme_light_secondary,
                onSecondary = md_theme_light_onSecondary,
                secondaryContainer = md_theme_light_secondaryContainer,
                onSecondaryContainer = md_theme_light_onSecondaryContainer,
                tertiary = md_theme_light_tertiary,
                onTertiary = md_theme_light_onTertiary,
                tertiaryContainer = md_theme_light_tertiaryContainer,
                onTertiaryContainer = md_theme_light_onTertiaryContainer,
                error = md_theme_light_error,
                onError = md_theme_light_onError,
                errorContainer = md_theme_light_errorContainer,
                onErrorContainer = md_theme_light_onErrorContainer,
                outline = md_theme_light_outline,
                background = md_theme_light_background,
                onBackground = md_theme_light_onBackground,
                surface = md_theme_light_surface,
                onSurface = md_theme_light_onSurface,
                surfaceVariant = md_theme_light_surfaceVariant,
                onSurfaceVariant = md_theme_light_onSurfaceVariant,
                inverseSurface = md_theme_light_inverseSurface,
                inverseOnSurface = md_theme_light_inverseOnSurface,
                inversePrimary = md_theme_light_inversePrimary,
                surfaceTint = md_theme_light_surfaceTint,
                outlineVariant = md_theme_light_outlineVariant,
                scrim = md_theme_light_scrim,
            )
            ColorPalette(
                title = "Dark Mode",
                primary = md_theme_dark_primary,
                onPrimary = md_theme_dark_onPrimary,
                primaryContainer = md_theme_dark_primaryContainer,
                onPrimaryContainer = md_theme_dark_onPrimaryContainer,
                secondary = md_theme_dark_secondary,
                onSecondary = md_theme_dark_onSecondary,
                secondaryContainer = md_theme_dark_secondaryContainer,
                onSecondaryContainer = md_theme_dark_onSecondaryContainer,
                tertiary = md_theme_dark_tertiary,
                onTertiary = md_theme_dark_onTertiary,
                tertiaryContainer = md_theme_dark_tertiaryContainer,
                onTertiaryContainer = md_theme_dark_onTertiaryContainer,
                error = md_theme_dark_error,
                onError = md_theme_dark_onError,
                errorContainer = md_theme_dark_errorContainer,
                onErrorContainer = md_theme_dark_onErrorContainer,
                outline = md_theme_dark_outline,
                background = md_theme_dark_background,
                onBackground = md_theme_dark_onBackground,
                surface = md_theme_dark_surface,
                onSurface = md_theme_dark_onSurface,
                surfaceVariant = md_theme_dark_surfaceVariant,
                onSurfaceVariant = md_theme_dark_onSurfaceVariant,
                inverseSurface = md_theme_dark_inverseSurface,
                inverseOnSurface = md_theme_dark_inverseOnSurface,
                inversePrimary = md_theme_dark_inversePrimary,
                surfaceTint = md_theme_dark_surfaceTint,
                outlineVariant = md_theme_dark_outlineVariant,
                scrim = md_theme_dark_scrim,
            )
        }
    }
}

@Composable
private fun ColorPalette(
    title: String,
    primary: Color,
    onPrimary: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    secondary: Color,
    onSecondary: Color,
    secondaryContainer: Color,
    onSecondaryContainer: Color,
    tertiary: Color,
    onTertiary: Color,
    tertiaryContainer: Color,
    onTertiaryContainer: Color,
    error: Color,
    onError: Color,
    errorContainer: Color,
    onErrorContainer: Color,
    outline: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color,
    surfaceVariant: Color,
    onSurfaceVariant: Color,
    inverseSurface: Color,
    inverseOnSurface: Color,
    inversePrimary: Color,
    surfaceTint: Color,
    outlineVariant: Color,
    scrim: Color,
) {
    val textModifier = Modifier.padding(
        Dimension.medium,
    ).fillMaxWidth()
    Column(
        modifier = Modifier.width(200.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.then(textModifier),
        )
        Text(
            text = "Primary",
            color = onPrimary,
            modifier = Modifier
                .background(primary)
                .then(textModifier),
        )
        Text(
            text = "PrimaryContainer",
            color = onPrimaryContainer,
            modifier = Modifier
                .background(primaryContainer)
                .then(textModifier),
        )
        Text(
            text = "Secondary",
            color = onSecondary,
            modifier = Modifier
                .background(secondary)
                .then(textModifier),
        )
        Text(
            text = "SecondaryContainer",
            color = onSecondaryContainer,
            modifier = Modifier
                .background(secondaryContainer)
                .then(textModifier),
        )
        Text(
            text = "Tertiary",
            color = onTertiary,
            modifier = Modifier
                .background(tertiary)
                .then(textModifier),
        )
        Text(
            text = "TertiaryContainer",
            color = onTertiaryContainer,
            modifier = Modifier
                .background(tertiaryContainer)
                .then(textModifier),
        )
        Text(
            text = "Error",
            color = onError,
            modifier = Modifier
                .background(error)
                .then(textModifier),
        )
        Text(
            text = "ErrorContainer",
            color = onErrorContainer,
            modifier = Modifier
                .background(errorContainer)
                .then(textModifier),
        )
        Text(
            text = "Background",
            color = onBackground,
            modifier = Modifier
                .background(background)
                .then(textModifier),
        )
        Text(
            text = "Surface",
            color = onSurface,
            modifier = Modifier
                .background(surface)
                .then(textModifier),
        )
        Text(
            text = "SurfaceVariant",
            color = onSurfaceVariant,
            modifier = Modifier
                .background(surfaceVariant)
                .then(textModifier),
        )
        Text(
            text = "InverseSurface",
            color = inverseOnSurface,
            modifier = Modifier
                .background(inverseSurface)
                .then(textModifier),
        )
        Text(
            text = "Outline",
            color = onPrimary,
            modifier = Modifier
                .background(outline)
                .then(textModifier),
        )
        Text(
            text = "InversePrimary",
            color = onPrimary,
            modifier = Modifier
                .background(inversePrimary)
                .then(textModifier),
        )
        Text(
            text = "SurfaceTint",
            color = onPrimary,
            modifier = Modifier
                .background(surfaceTint)
                .then(textModifier),
        )
        Text(
            text = "OutlineVariant",
            color = onPrimary,
            modifier = Modifier
                .background(outlineVariant)
                .then(textModifier),
        )
        Text(
            text = "Scrim",
            color = onPrimary,
            modifier = Modifier
                .background(scrim)
                .then(textModifier),
        )
    }
}
