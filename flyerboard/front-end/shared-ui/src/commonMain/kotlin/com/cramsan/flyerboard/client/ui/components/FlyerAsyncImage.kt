package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage

@Composable
fun FlyerAsyncImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val imageModifier = modifier.fillMaxWidth().aspectRatio(FLYER_ASPECT_RATIO)
    if (url != null) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = imageModifier,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            },
        )
    } else {
        Box(
            modifier = imageModifier.background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

private const val FLYER_ASPECT_RATIO = 4f / 3f
