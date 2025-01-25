package com.cramsan.framework.core.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * Produce a preview of the color scheme.
 * This function is intended to be used in a preview or debug screen.
 */
@Composable
fun ColorPreviewer(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
) {
    Row {
        Column {
            ColorPreview(colorScheme.primary, "primary")
            ColorPreview(colorScheme.onPrimary, "onPrimary")
            ColorPreview(colorScheme.primaryContainer, "primaryContainer")
            ColorPreview(colorScheme.onPrimaryContainer, "onPrimaryContainer")
            ColorPreview(colorScheme.inversePrimary, "inversePrimary")
            ColorPreview(colorScheme.secondary, "secondary")
            ColorPreview(colorScheme.onSecondary, "onSecondary")
            ColorPreview(colorScheme.secondaryContainer, "secondaryContainer")
            ColorPreview(colorScheme.onSecondaryContainer, "onSecondaryContainer")
            ColorPreview(colorScheme.tertiary, "tertiary")
            ColorPreview(colorScheme.onTertiary, "onTertiary")
            ColorPreview(colorScheme.tertiaryContainer, "tertiaryContainer")
            ColorPreview(colorScheme.onTertiaryContainer, "onTertiaryContainer")
            ColorPreview(colorScheme.background, "background")
            ColorPreview(colorScheme.onBackground, "onBackground")
            ColorPreview(colorScheme.surface, "surface")
            ColorPreview(colorScheme.onSurface, "onSurface")
            ColorPreview(colorScheme.surfaceVariant, "surfaceVariant")
            ColorPreview(colorScheme.onSurfaceVariant, "onSurfaceVariant")
        }
        Column {
            ColorPreview(colorScheme.surfaceTint, "surfaceTint")
            ColorPreview(colorScheme.inverseSurface, "inverseSurface")
            ColorPreview(colorScheme.inverseOnSurface, "inverseOnSurface")
            ColorPreview(colorScheme.error, "error")
            ColorPreview(colorScheme.onError, "onError")
            ColorPreview(colorScheme.errorContainer, "errorContainer")
            ColorPreview(colorScheme.onErrorContainer, "onErrorContainer")
            ColorPreview(colorScheme.outline, "outline")
            ColorPreview(colorScheme.outlineVariant, "outlineVariant")
            ColorPreview(colorScheme.scrim, "scrim")
            ColorPreview(colorScheme.surfaceBright, "surfaceBright")
            ColorPreview(colorScheme.surfaceDim, "surfaceDim")
            ColorPreview(colorScheme.surfaceContainer, "surfaceContainer")
            ColorPreview(colorScheme.surfaceContainerHigh, "surfaceContainerHigh")
            ColorPreview(colorScheme.surfaceContainerHighest, "surfaceContainerHighest")
            ColorPreview(colorScheme.surfaceContainerLow, "surfaceContainerLow")
            ColorPreview(colorScheme.surfaceContainerLowest, "surfaceContainerLowest")
        }
    }
}

/**
 * Produce a preview of the shapes.
 * This function is intended to be used in a preview or debug screen.
 */
@Composable
fun ShapePreviewer(
    shapes: Shapes = MaterialTheme.shapes,
) {
    Column {
        ShapePreview(shapes.extraSmall, "extraSmall")
        ShapePreview(shapes.small, "small")
        ShapePreview(shapes.medium, "medium")
        ShapePreview(shapes.large, "large")
        ShapePreview(shapes.extraLarge, "extraLarge")
    }
}

/**
 * Produce a preview of the typography.
 * This function is intended to be used in a preview or debug screen.
 */
@Composable
fun TypographyPreviewer(
    typography: Typography = MaterialTheme.typography,
) {
    Column {
        TypographyPreview(typography.displayLarge, "displayLarge")
        TypographyPreview(typography.displayMedium, "displayMedium")
        TypographyPreview(typography.displaySmall, "displaySmall")
        TypographyPreview(typography.headlineLarge, "headlineLarge")
        TypographyPreview(typography.headlineMedium, "headlineMedium")
        TypographyPreview(typography.headlineSmall, "headlineSmall")
        TypographyPreview(typography.titleLarge, "titleLarge")
        TypographyPreview(typography.titleMedium, "titleMedium")
        TypographyPreview(typography.titleSmall, "titleSmall")
        TypographyPreview(typography.bodyLarge, "bodyLarge")
        TypographyPreview(typography.bodyMedium, "bodyMedium")
        TypographyPreview(typography.bodySmall, "bodySmall")
        TypographyPreview(typography.labelLarge, "labelLarge")
        TypographyPreview(typography.labelMedium, "labelMedium")
        TypographyPreview(typography.labelSmall, "labelSmall")
    }
}

@Composable
private fun ColorPreview(color: Color, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(50.dp).background(color),
        )
        Text(name)
    }
}

@Composable
private fun ShapePreview(shape: Shape, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.padding(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = shape),
            )
        }
        Text(name)
    }
}

@Composable
private fun TypographyPreview(textStyle: TextStyle, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, style = textStyle)
    }
}
