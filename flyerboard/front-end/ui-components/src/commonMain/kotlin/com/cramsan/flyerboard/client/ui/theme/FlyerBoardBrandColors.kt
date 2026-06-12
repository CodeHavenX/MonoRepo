@file:Suppress("MagicNumber")

package com.cramsan.flyerboard.client.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.sp

/** Dark-navy background shared by the branded top bar and footer. */
internal val FlyerBoardNavyBackground = Color(0xFF1E1B4B)

/** Gradient accent colors used for the thin accent line below the branded top bar and the splash screen. */
internal val FlyerBoardAccentGradientStart = Color(0xFF4F46E5)
internal val FlyerBoardAccentGradientEnd = Color(0xFF7C3AED)

/** Primary text/icon color on dark-navy branded surfaces (top bar, footer). */
internal val FlyerBoardOnNavy = Color.White

/** Muted text color for secondary copy (e.g. footer copyright) on dark-navy branded surfaces. */
internal val FlyerBoardOnNavyMuted = FlyerBoardOnNavy.copy(alpha = 0.75f)

/** Text color for footer links on dark-navy branded surfaces. */
internal val FlyerBoardOnNavyLink = FlyerBoardOnNavy.copy(alpha = 0.85f)

/** Background overlay for the selected tab in the branded top bar. */
internal val FlyerBoardSelectedTabOverlay = FlyerBoardOnNavy.copy(alpha = 0.15f)

/** Letter spacing applied to the FLYERBOARD wordmark. */
internal val FlyerBoardWordmarkLetterSpacing = 1.sp

/** Pill shape used for buttons and tabs on branded surfaces. */
internal val FlyerBoardPillShape: Shape = RoundedCornerShape(percent = 50)
