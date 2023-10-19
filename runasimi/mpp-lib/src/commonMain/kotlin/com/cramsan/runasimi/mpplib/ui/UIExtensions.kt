package com.cramsan.runasimi.mpplib.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cramsan.runasimi.mpplib.ui.theme.component_dark_blue
import com.cramsan.runasimi.mpplib.ui.theme.component_dark_red
import com.cramsan.runasimi.mpplib.ui.theme.component_dark_yellow
import com.cramsan.runasimi.mpplib.ui.theme.component_light_blue
import com.cramsan.runasimi.mpplib.ui.theme.component_light_red
import com.cramsan.runasimi.mpplib.ui.theme.component_light_yellow

fun List<CardUiModel.Word>.toSentenceString(): String {
    return joinToString(" ") {
        it.segments.joinToString("") { it.segment }
    }
}

@Composable
fun ComponentColor.toColor(): Color {
    return if (isSystemInDarkTheme()) {
        when (this) {
            ComponentColor.YELLOW -> component_dark_yellow
            ComponentColor.BLUE -> component_dark_blue
            ComponentColor.RED -> component_dark_red
        }
    } else {
        when (this) {
            ComponentColor.YELLOW -> component_light_yellow
            ComponentColor.BLUE -> component_light_blue
            ComponentColor.RED -> component_light_red
        }
    }
}
