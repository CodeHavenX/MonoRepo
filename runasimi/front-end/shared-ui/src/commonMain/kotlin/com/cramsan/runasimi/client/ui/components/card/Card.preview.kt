package com.cramsan.runasimi.client.ui.components.card

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun CardPreview_Front() {
    Card(
        "Iskay chunka pichqayuq",
        "25",
        startInFront = true,
    )
}

@Preview
@Composable
private fun CardPreview_Back() {
    Card(
        "Iskay chunka pichqayuq",
        "25",
        startInFront = false,
    )
}
