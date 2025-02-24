package com.cramsan.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.ui.theme.Padding

/**
 * This component represents the content section within a two-part screen(top section and bottom actions).
 *
 * This component will define the style and padding for the content section. The [content] will get a [Modifier] that
 * will define the padding and width of the content.
 */
@Composable
fun ContentSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(Modifier) -> Unit,
) {
    Column(
        modifier = modifier,
        content = {
            content(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Padding.MEDIUM,
                        vertical = Padding.X_SMALL,
                    )
            )
        },
    )
}
