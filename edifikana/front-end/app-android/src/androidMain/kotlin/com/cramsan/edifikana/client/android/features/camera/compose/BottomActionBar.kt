package com.cramsan.edifikana.client.android.features.camera.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FlipCameraAndroid
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BottomActionBar(
    modifier: Modifier = Modifier,
    tertiaryButton: @Composable (BoxScope.() -> Unit)? = null,
    secondaryButton: @Composable (BoxScope.() -> Unit)? = null,
    mainButton: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
            content = tertiaryButton ?: {},
        )
        mainButton()
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
            content = secondaryButton ?: {},
        )
    }
}

@Preview
@Composable
fun PreviewBottomActionBar() {
    BottomActionBar(
        mainButton = {
            IconButton(
                onClick = { },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Lens,
                        contentDescription = "",
                        tint = Color.White,
                    )
                }
            )
        },
        secondaryButton = {
            IconButton(
                onClick = { },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.FlipCameraAndroid,
                        contentDescription = "",
                        tint = Color.White,
                    )
                }
            )
        }
    )
}
