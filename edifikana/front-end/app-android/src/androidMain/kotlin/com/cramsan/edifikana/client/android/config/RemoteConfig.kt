package com.cramsan.edifikana.client.android.config

import android.graphics.Bitmap
import androidx.camera.core.ImageCapture

/**
 * Root class for all remote configuration.
 */
data class RemoteConfig(
    val image: ImageConfig,
    val caching: CachingConfig,
)

/**
 * The configuration data for compression and caching.
 *
 * The [imageQualityHint] is a compression-format agnostic way to define the level of quality of the compressed image.
 *
 * @see [Bitmap.compress]
 */
data class CachingConfig(
    val imageQualityHint: Int,
)

/**
 * Class that represents the preferred size at which the images will be taken. This is important as most of the
 * devices running the app will be lower-end.
 *
 * For [captureWidth] and [captureHeight], their usage is dictated by the requirements of the
 * [ImageCapture.Builder.setTargetResolution] API. In general terms, these dimensions define the min size of the image
 * while fitting the largest dimension and keeping the aspect ratio.
 */
data class ImageConfig(
    val captureWidth: Int,
    val captureHeight: Int,
)
