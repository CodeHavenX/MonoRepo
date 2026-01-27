package com.cramsan.edifikana.client.ui.resources

import edifikana_ui.L_Depa_city
import edifikana_ui.M_Depa_city
import edifikana_ui.Res
import edifikana_ui.S_Depa_city
import edifikana_ui.casa_city
import edifikana_ui.quinta_city
import org.jetbrains.compose.resources.DrawableResource

/**
 * Property icon resources for the Edifikana application.
 *
 * These icons represent different property types and are located in the
 * shared-ui composeResources/drawable directory.
 *
 * This object also provides conversion functions between DrawableResource
 * and the string format used by the API ("drawable:ICON_NAME").
 */
object PropertyIcons {
    /**
     * Casa (House) property icon
     * Resource: casa-city.png
     */
    val CASA: DrawableResource = Res.drawable.casa_city

    /**
     * Quinta property icon
     * Resource: quinta-city.png
     */
    val QUINTA: DrawableResource = Res.drawable.quinta_city

    /**
     * Large Department (L-Depa) property icon
     * Resource: L-Depa-city.png
     */
    val L_DEPA: DrawableResource = Res.drawable.L_Depa_city

    /**
     * Medium Department (M-Depa) property icon
     * Resource: M-Depa-city.png
     */
    val M_DEPA: DrawableResource = Res.drawable.M_Depa_city

    /**
     * Small Department (S-Depa) property icon
     * Resource: S-Depa-city.png
     */
    val S_DEPA: DrawableResource = Res.drawable.S_Depa_city

    /**
     * Icon identifier constants matching the API string format.
     */
    const val CASA_ID = "CASA"
    const val QUINTA_ID = "QUINTA"
    const val L_DEPA_ID = "L_DEPA"
    const val M_DEPA_ID = "M_DEPA"
    const val S_DEPA_ID = "S_DEPA"

    /**
     * Map of icon IDs to their corresponding DrawableResource.
     */
    private val iconMap = mapOf(
        CASA_ID to CASA,
        QUINTA_ID to QUINTA,
        L_DEPA_ID to L_DEPA,
        M_DEPA_ID to M_DEPA,
        S_DEPA_ID to S_DEPA,
    )

    /**
     * Get the DrawableResource for a given icon ID.
     *
     * @param iconId The icon identifier (e.g., "CASA", "QUINTA")
     * @return The corresponding DrawableResource, or null if not found
     */
    fun getDrawableById(iconId: String): DrawableResource? = iconMap[iconId]

    /**
     * Get the icon ID for a given DrawableResource.
     *
     * @param drawable The DrawableResource
     * @return The corresponding icon ID, or null if not found
     */
    fun getIdByDrawable(drawable: DrawableResource): String? = iconMap.entries.firstOrNull { it.value == drawable }?.key

    /**
     * Get all available property icons as a map of ID to DrawableResource.
     */
    fun getAllIcons(): Map<String, DrawableResource> = iconMap
}
