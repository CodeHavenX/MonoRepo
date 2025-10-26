package com.cramsan.runasimi.client.ui.components.card

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cramsan.ui.components.flippable.Flippable
import com.cramsan.ui.components.flippable.rememberFlipController
import com.cramsan.ui.theme.Size

/**
 * A card that can be flipped to show either the Quechua text or the translated text.
 *
 * @param quechua The Quechua text to display on the front side of the card.
 * @param translated The translated text to display on the back side of the card.
 * @param startInFront Whether the card should start showing the front side (Quechua) or back side (translated).
 * @param modifier Modifier to be applied to the card.
 */
@Composable
fun Card(
    quechua: String?,
    translated: String?,
    startInFront: Boolean,
    modifier: Modifier = Modifier,
) {
    val flipController = rememberFlipController()
    LaunchedEffect(quechua, translated, startInFront) {
        if (startInFront) {
            flipController.flipToFront()
        } else {
            flipController.flipToBack()
        }
    }
    Flippable(
        frontSide = {
            CardSide(quechua)
        },
        backSide = {
            CardSide(translated)
        },
        flipDurationMs = 200,
        flipController = flipController,
        modifier = modifier,
    )
}

@Composable
private fun CardSide(
    text: String?,
) {
    Box(
        modifier = Modifier
            .shadow(15.dp, MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                2.dp,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.shapes.large,
            )
            .sizeIn(maxWidth = 400.dp, maxHeight = 250.dp)
            .fillMaxSize()
            .padding(Size.large),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            text,
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = it.orEmpty(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}
