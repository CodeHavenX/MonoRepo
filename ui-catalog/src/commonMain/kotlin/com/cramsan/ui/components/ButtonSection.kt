package com.cramsan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.ui.theme.Padding

/**
 * This component represents the action section within a two-part screen(top section and bottom actions).
 *
 * This component will define the style and padding for the action section. The [buttons] will get a [Modifier] that
 * will define the padding and width of the content.
 */
@Composable
fun ButtonSection(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Padding.SMALL),
    buttons: @Composable ColumnScope.(Modifier) -> Unit,
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = Padding.MEDIUM,
        )
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        content = {
            buttons(debugModifier(buttonModifier))
        },
    )
}
