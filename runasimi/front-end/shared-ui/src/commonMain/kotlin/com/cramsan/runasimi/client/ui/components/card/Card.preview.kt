package com.cramsan.runasimi.client.ui.components.card

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews

@ComponentPreviews
@Composable
private fun CardPreview_Front() {
    Card(
        "Iskay chunka pichqayuq",
        "25",
        startInFront = true,
    )
}

@ComponentPreviews
@Composable
private fun CardPreview_Back() {
    Card(
        "Iskay chunka pichqayuq",
        "25",
        startInFront = false,
    )
}
