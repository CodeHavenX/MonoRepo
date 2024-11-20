package com.codehavenx.alpaca.frontend.appcore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding

/**
 * A list item component.
 *
 * @param title The title of the list item.
 * @param modifier The modifier for the list item.
 * @param style The style for the title.
 * @param subtitle The subtitle of the list item.
 * @param subtitleStyle The style for the subtitle.
 * @param onClick The action to perform when the list item is clicked.
 */
@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    subtitle: String? = null,
    subtitleStyle: TextStyle = MaterialTheme.typography.labelMedium,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                if (onClick != null) {
                    clickable(onClick = onClick)
                } else {
                    this
                }
            }
            .padding(Padding.medium)
            .then(modifier),
    ) {
        Column {
            Text(
                title,
                style = style,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    subtitle,
                    style = subtitleStyle,
                )
            }
        }
    }
}
