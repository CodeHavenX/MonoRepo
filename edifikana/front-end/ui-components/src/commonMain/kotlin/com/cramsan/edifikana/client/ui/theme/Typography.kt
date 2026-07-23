@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Custom typography tokens not covered by Material3's default type scale.
 */
object Typography {
    /** Emphasized card/section title, e.g. an organization name header. */
    val cardTitle: TextStyle =
        TextStyle(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
        )
}
