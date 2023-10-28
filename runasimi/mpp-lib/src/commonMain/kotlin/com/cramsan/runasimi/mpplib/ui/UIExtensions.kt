package com.cramsan.runasimi.mpplib.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cramsan.runasimi.mpplib.ui.theme.component_type_a_dark
import com.cramsan.runasimi.mpplib.ui.theme.component_type_a_light
import com.cramsan.runasimi.mpplib.ui.theme.component_type_b_dark
import com.cramsan.runasimi.mpplib.ui.theme.component_type_b_light
import com.cramsan.runasimi.mpplib.ui.theme.component_type_c_dark
import com.cramsan.runasimi.mpplib.ui.theme.component_type_c_light

fun List<CardUiModel.Word>.toSentenceString(): String {
    return joinToString(" ") {
        it.segments.joinToString("") { it.segment }
    }
}

@Composable
fun ComponentColor.toColor(): Color {
    return if (isSystemInDarkTheme()) {
        when (this) {
            ComponentColor.YELLOW -> component_type_a_dark
            ComponentColor.BLUE -> component_type_b_dark
            ComponentColor.RED -> component_type_c_dark
        }
    } else {
        when (this) {
            ComponentColor.YELLOW -> component_type_a_light
            ComponentColor.BLUE -> component_type_b_light
            ComponentColor.RED -> component_type_c_light
        }
    }
}
