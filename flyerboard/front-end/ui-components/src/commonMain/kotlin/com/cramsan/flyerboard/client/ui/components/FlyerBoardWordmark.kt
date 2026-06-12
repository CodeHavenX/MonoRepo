package com.cramsan.flyerboard.client.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardOnNavy
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardWordmarkLetterSpacing

/** "FLYERBOARD" wordmark shown on dark-navy branded surfaces (top bar, footer). */
@Composable
fun FlyerBoardWordmark(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "FLYERBOARD",
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = FlyerBoardWordmarkLetterSpacing,
        color = FlyerBoardOnNavy,
    )
}
