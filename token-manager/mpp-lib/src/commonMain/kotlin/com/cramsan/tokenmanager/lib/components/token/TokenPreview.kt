package com.cramsan.tokenmanager.lib.components.token

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.tokenmanager.lib.state.TokenModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun TokenPreview() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Token(TokenModel())
    }
}

@Preview
@Composable
private fun TokenExpandedPreview() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Token(
            TokenModel(),
            expand = true,
        )
    }
}

@Preview
@Composable
private fun TokenDragPreview() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .size(200.dp)
    ) {
        Token(TokenModel())
    }
}
