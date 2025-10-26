package com.cramsan.ui.components.flippable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FlippablePreview() {
    Box(Modifier.fillMaxSize()) {
        Flippable(
            frontSide = {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Front Side",
                        color = Color.White,
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    )
                }
            },
            backSide = {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color(0xFFF44336)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Back Side",
                        color = Color.White,
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    )
                }
            },
            flipDurationMs = 2000,
            flipController = rememberFlipController(),
        )
    }
}
