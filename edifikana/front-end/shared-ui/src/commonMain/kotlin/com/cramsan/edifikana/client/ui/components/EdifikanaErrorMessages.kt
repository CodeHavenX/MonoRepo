package com.cramsan.edifikana.client.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Displays a list of error messages with a fade animation. Renders nothing when [messages] is null or empty.
 */
@Composable
fun EdifikanaErrorMessages(
    messages: List<String>?,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        messages,
        modifier = modifier,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) {
        if (!it.isNullOrEmpty()) {
            it.forEach { errorMessage ->
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
