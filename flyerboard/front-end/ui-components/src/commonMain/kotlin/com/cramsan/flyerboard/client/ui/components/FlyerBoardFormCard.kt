package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size

/**
 * Scaffold for a screen centered around a single form card, e.g. sign in, sign up, or
 * flyer submission. Renders a [title] and [subtitle] above [content], with a [LoadingAnimationOverlay]
 * driven by [isLoading].
 *
 * [overlayContent] is drawn behind the card and can be used for elements positioned relative to
 * the full screen, such as a debug button anchored to a corner.
 */
@Composable
fun FlyerBoardFormCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    overlayContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center,
        ) {
            overlayContent()
            Card(
                modifier =
                Modifier
                    .widthIn(max = Size.COLUMN_MAX_WIDTH)
                    .padding(Padding.LARGE),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = Padding.X_SMALL),
                colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Padding.XX_SMALL)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Column(
                    modifier = Modifier.padding(Padding.LARGE),
                    verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(Padding.X_SMALL))
                    content()
                }
            }
            LoadingAnimationOverlay(isLoading)
        }
    }
}
