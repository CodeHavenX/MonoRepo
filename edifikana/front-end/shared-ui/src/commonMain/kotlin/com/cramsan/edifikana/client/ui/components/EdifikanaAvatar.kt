package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Edifikana avatar component for displaying circular profile images.
 *
 * Uses [EdifikanaImage] internally with [ImageSource.Url] for the provided URL string.
 *
 * @param imageUrl URL of the image to display
 * @param contentDescription Content description for accessibility
 * @param modifier Modifier for the avatar
 * @param size Size of the avatar (default 120.dp for profile screens)
 */
@Composable
fun EdifikanaAvatar(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        val imageSource = if (imageUrl != null) {
            ImageSource.Url(imageUrl)
        } else {
            ImageSource.None
        }
        EdifikanaImage(
            imageSource = imageSource,
            contentDescription = contentDescription,
            size = size,
            contentScale = ContentScale.Crop,
        )
    }
}
