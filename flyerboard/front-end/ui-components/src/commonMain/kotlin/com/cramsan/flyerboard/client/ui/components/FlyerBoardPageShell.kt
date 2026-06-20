package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size

/**
 * Scaffold-free page shell providing the shared visual chrome used across content and form
 * screens: a [MaterialTheme.colorScheme.surfaceVariant] background, a centered and max-width
 * [Card] with a primary-color accent bar at its top edge, card elevation, and vertical scroll.
 *
 * Callers are responsible for their own [androidx.compose.material3.Scaffold] and any loading
 * overlay. The [content] slot is a [ColumnScope] placed directly inside the card after the accent
 * bar, with no forced inner padding, so callers control layout fully (e.g. a full-bleed image
 * followed by a padded text column).
 *
 * [contentAlignment] controls how the card is aligned inside the scrollable area.
 * Use [Alignment.Center] for short forms; [Alignment.TopCenter] for longer scrollable content.
 *
 * [cardPadding] is the outer padding between the screen edge and the card.
 *
 * [overlayContent] is drawn as a sibling of the card inside the outer [Box], useful for
 * full-screen overlays or corner-anchored debug buttons.
 */
@Composable
fun FlyerBoardPageShell(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    cardPadding: Dp = Padding.LARGE,
    overlayContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier =
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(rememberScrollState()),
        contentAlignment = contentAlignment,
    ) {
        overlayContent()
        Card(
            modifier =
            Modifier
                .widthIn(max = Size.COLUMN_MAX_WIDTH)
                .padding(cardPadding),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = Padding.X_SMALL),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Padding.XX_SMALL)
                        .background(MaterialTheme.colorScheme.primary),
                )
                content()
            }
        }
    }
}
