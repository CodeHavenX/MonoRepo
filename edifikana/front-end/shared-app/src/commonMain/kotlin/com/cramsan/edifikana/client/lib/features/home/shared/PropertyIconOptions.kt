package com.cramsan.edifikana.client.lib.features.home.shared

import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.edifikana.client.ui.resources.PropertyIcons

/**
 * Helper object for property icon selection.
 *
 * Provides conversion between ImageOptionUIModel (used in UI) and
 * the string format used by the API ("drawable:ICON_NAME").
 */
object PropertyIconOptions {

    /**
     * Get the list of default property icon options for the dropdown.
     *
     * @return List of ImageOptionUIModel representing available property icons
     */
    fun getDefaultOptions(): List<ImageOptionUIModel> = PropertyIcons.getAllIcons().map { (id, drawable) ->
        ImageOptionUIModel(
            id = id,
            displayName = getDisplayName(id),
            imageSource = ImageSource.Drawable(drawable),
        )
    }

    /**
     * Get the list of property icon options including the upload option.
     *
     * @return List of ImageOptionUIModel including default icons and an "Upload Custom Image" option
     */
    fun getOptionsWithUpload(): List<ImageOptionUIModel> {
        val defaultOptions = getDefaultOptions()
        val uploadOption = ImageOptionUIModel(
            id = "custom_upload",
            displayName = "Upload Custom Image",
            imageSource = ImageSource.None,
        )
        return defaultOptions + uploadOption
    }

    /**
     * Convert an ImageOptionUIModel to the API string format.
     *
     * @param option The selected image option
     * @return API-compatible string (e.g., "drawable:CASA") or null if option is null
     */
    fun toImageUrl(option: ImageOptionUIModel?): String? {
        return when (option?.imageSource) {
            is ImageSource.Drawable -> "drawable:${option.id}"
            is ImageSource.Url -> (option.imageSource as ImageSource.Url).url
            is ImageSource.LocalFile -> null // LocalFile should be uploaded first before calling this
            is ImageSource.None, null -> null
        }
    }

    /**
     * Convert an API string to an ImageOptionUIModel.
     *
     * @param imageUrl API string (e.g., "drawable:CASA", "https://...", or null)
     * @return Corresponding ImageOptionUIModel, or null if no match
     */
    fun fromImageUrl(imageUrl: String?): ImageOptionUIModel? {
        if (imageUrl == null) return null

        // Check if it's a drawable reference
        if (imageUrl.startsWith("drawable:")) {
            val iconId = imageUrl.removePrefix("drawable:")
            val drawable = PropertyIcons.getDrawableById(iconId) ?: return null
            return ImageOptionUIModel(
                id = iconId,
                displayName = getDisplayName(iconId),
                imageSource = ImageSource.Drawable(drawable),
            )
        }

        // Check if it's a URL
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return ImageOptionUIModel(
                id = "custom_url",
                displayName = "Custom Image",
                imageSource = ImageSource.Url(imageUrl),
            )
        }

        return null
    }

    /**
     * Get a human-readable display name for an icon ID.
     */
    private fun getDisplayName(iconId: String): String = when (iconId) {
        PropertyIcons.CASA_ID -> "Casa"
        PropertyIcons.QUINTA_ID -> "Quinta"
        PropertyIcons.L_DEPA_ID -> "Large Department"
        PropertyIcons.M_DEPA_ID -> "Medium Department"
        PropertyIcons.S_DEPA_ID -> "Small Department"
        else -> iconId
    }
}
