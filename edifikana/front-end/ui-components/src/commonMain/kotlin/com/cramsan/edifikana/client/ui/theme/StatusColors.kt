@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Semantic status colors (success, warning) not represented in Material3's default `ColorScheme`.
 */
object StatusColors {
    /** Background for a positive/active state, e.g. an "Active" badge. */
    val successContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) successContainerDark else successContainerLight

    /** Content color to pair with [successContainer]. */
    val onSuccessContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) onSuccessContainerDark else onSuccessContainerLight

    /** Background for a cautionary state, e.g. an ownership-transfer action icon. */
    val warningContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) warningContainerDark else warningContainerLight

    /** Content color to pair with [warningContainer]. */
    val onWarningContainer: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) onWarningContainerDark else onWarningContainerLight
}

private val successContainerLight = Color(0xFFDCFCE7)
private val onSuccessContainerLight = Color(0xFF16A34A)
private val successContainerDark = Color(0xFF14532D)
private val onSuccessContainerDark = Color(0xFF86EFAC)

private val warningContainerLight = Color(0xFFFEF3C7)
private val onWarningContainerLight = Color(0xFFF59E0B)
private val warningContainerDark = Color(0xFF78350F)
private val onWarningContainerDark = Color(0xFFFCD34D)
