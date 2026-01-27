package com.cramsan.edifikana.client.lib.managers.remoteconfig

/**
 * Root class for all remote configuration.
 */
data class RemoteConfig(
    val image: ImageConfig,
    val caching: CachingConfig,
    val behavior: BehaviorConfig,
    val features: FeatureConfig,
)

/**
 * The configuration data for the behavior of the app.
 *
 * The [fetchPeriod] is the period in days to retrieve data for.
 */
data class BehaviorConfig(val fetchPeriod: Int, val allowListedCodes: List<String>)

/**
 * The configuration data for compression and caching.
 *
 * The [imageQualityHint] is a compression-format agnostic way to define the level of quality of the compressed image.
 *
 * @see [Bitmap.compress]
 */
data class CachingConfig(val imageQualityHint: Int)

/**
 * Class that represents the preferred size at which the images will be taken. This is important as most of the
 * devices running the app will be lower-end.
 *
 * For [captureWidth] and [captureHeight], their usage is dictated by the requirements of the
 * [ImageCapture.Builder.setTargetResolution] API. In general terms, these dimensions define the min size of the image
 * while fitting the largest dimension and keeping the aspect ratio.
 */
data class ImageConfig(val captureWidth: Int, val captureHeight: Int)

/**
 * The configuration data for the features of the app.
 */
data class FeatureConfig(val flags: Map<Features, Boolean>) {
    /**
     * Check if a feature is enabled.
     */
    fun isFeatureEnabled(feature: Features): Boolean = flags[feature] ?: false
}
