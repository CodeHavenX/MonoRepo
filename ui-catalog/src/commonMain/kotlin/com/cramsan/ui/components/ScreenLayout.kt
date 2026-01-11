package com.cramsan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cramsan.framework.core.compose.ifNotTrue
import com.cramsan.framework.core.compose.ifTrue
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size

/**
 * This component represents the layout of an entire screen. It will contain two sections: the content section and the
 * action section. Optionally, an overlay composable can be provided to display content above the screen layout.
 */
@Composable
fun ScreenLayout(
    modifier: Modifier = Modifier,
    fixedFooter: Boolean = false,
    maxWith: Dp = Size.COLUMN_MAX_WIDTH,
    topPadding: Dp = Padding.X_LARGE,
    bottomPadding: Dp = Padding.X_LARGE,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Padding.MEDIUM),
    sectionContent: @Composable ColumnScope.(Modifier) -> Unit,
    buttonContent: (@Composable ColumnScope.(Modifier) -> Unit)? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.TopCenter,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        val screenLayoutModifier = Modifier
            .ifTrue(maxWith != Dp.Unspecified) {
                sizeIn(maxWidth = maxWith)
            }
            .padding(
                top = topPadding,
                bottom = bottomPadding,
            )
            .ifNotTrue(fixedFooter) {
                verticalScroll(rememberScrollState())
            }
        Column(
            modifier = debugModifier(screenLayoutModifier),
            verticalArrangement = verticalArrangement,
        ) {
            val sectionModifier = Modifier.ifTrue(fixedFooter) {
                weight(1f)
                    .verticalScroll(rememberScrollState())
            }
            ContentSection(
                modifier = debugModifier(sectionModifier),
                content = sectionContent,
            )
            buttonContent?.let {
                val actionModifier = Modifier
                ButtonSection(
                    modifier = debugModifier(actionModifier),
                    buttons = it,
                )
            }
        }
        overlay?.invoke(this)
    }
}

/**
 * Applies a debug modifier to the given [modifier] if the `LocalDebugLayoutInspection` is enabled.
 * This modifier adds a green border around the component, a semi-transparent gray background,
 * and a blue border around the component for visual inspection during debugging.
 *
 * @param modifier The original [Modifier] to which the debug properties will be applied.
 * @return A [Modifier] with debug properties applied if `LocalDebugLayoutInspection` is enabled, otherwise the original [Modifier].
 */
@Composable
fun debugModifier(modifier: Modifier = Modifier): Modifier {
    val enableInspectionMode = LocalDebugLayoutInspection.current
    return if (enableInspectionMode) {
        Modifier
            .border(1.dp, Color.Green)
            .background(Color.Gray.copy(alpha = 0.25f))
            .then(modifier)
            .border(1.dp, Color.Blue)
    } else {
        modifier
    }
}

val LocalDebugLayoutInspection = compositionLocalOf { false }
