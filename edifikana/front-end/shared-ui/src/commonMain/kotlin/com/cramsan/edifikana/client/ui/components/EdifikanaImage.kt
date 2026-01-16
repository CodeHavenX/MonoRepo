package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cramsan.edifikana.client.ui.resources.PropertyIcons
import org.jetbrains.compose.resources.painterResource

/**
 * Edifikana image component that intelligently handles different image sources.
 *
 * This component provides optimized image loading based on the source type:
 * - Drawable resources: Uses synchronous Image + painterResource (fast, efficient)
 * - URLs: Uses asynchronous AsyncImage with Coil (network loading)
 * - None/Placeholder: Shows S-Depa icon as default
 *
 * @param imageSource The source of the image (Drawable, Url, None, or UploadPlaceholder)
 * @param contentDescription Accessibility description for screen readers
 * @param modifier Modifier for the component
 * @param size Optional fixed size (applies both width and height)
 * @param cornerRadius Corner radius for rounded images (default: 0.dp)
 * @param contentScale How to scale the image (default: Fit)
 */
@Composable
fun EdifikanaImage(
    imageSource: ImageSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    cornerRadius: Dp = 0.dp,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val sizeModifier = if (size != null) Modifier.size(size) else Modifier
    val shape = if (cornerRadius > 0.dp) RoundedCornerShape(cornerRadius) else null
    val clipModifier = if (shape != null) Modifier.clip(shape) else Modifier

    val finalModifier = modifier.then(sizeModifier).then(clipModifier)

    when (imageSource) {
        is ImageSource.Drawable -> {
            // Synchronous loading for bundled resources - fast and efficient
            Image(
                painter = painterResource(imageSource.resource),
                contentDescription = contentDescription,
                modifier = finalModifier,
                contentScale = contentScale,
            )
        }
        is ImageSource.Url -> {
            // Asynchronous loading for remote URLs
            AsyncImage(
                model = imageSource.url,
                contentDescription = contentDescription,
                modifier = finalModifier,
                contentScale = contentScale,
            )
        }
        is ImageSource.None -> {
            // Default icon for "no image" state - uses S-Depa icon
            Image(
                painter = painterResource(PropertyIcons.S_DEPA),
                contentDescription = contentDescription,
                modifier = finalModifier,
                contentScale = contentScale,
            )
        }
    }
}
