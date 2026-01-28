package com.cramsan.edifikana.client.ui.components

import com.cramsan.framework.core.CoreUri
import org.jetbrains.compose.resources.DrawableResource

/**
 * Generic model for image selection options in dropdowns.
 * Can represent drawable resources, URLs, or special states.
 *
 * This model is designed to be reusable across different features
 * (properties, organizations, staff, etc.) that need image selection.
 */
data class ImageOptionUIModel(
    /**
     * Unique identifier for this option (e.g., "CASA", "custom_upload", "none").
     */
    val id: String,

    /**
     * Human-readable label displayed in the dropdown (e.g., "Casa", "Upload Custom Image").
     */
    val displayName: String,

    /**
     * The source of the image to display.
     */
    val imageSource: ImageSource,
)

/**
 * Represents different types of image sources.
 */
sealed class ImageSource {
    /**
     * A drawable resource bundled with the app.
     */
    data class Drawable(val resource: DrawableResource) : ImageSource()

    /**
     * An image loaded from a URL.
     */
    data class Url(val url: String) : ImageSource()

    /**
     * No image - used for "None" or default options.
     */
    data object None : ImageSource()

    /**
     * A local file selected by the user, pending upload.
     * Contains the URI and filename for display and processing.
     */
    data class LocalFile(val uri: CoreUri, val filename: String) : ImageSource()
}
