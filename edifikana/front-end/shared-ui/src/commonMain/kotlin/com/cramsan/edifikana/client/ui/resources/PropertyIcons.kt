package com.cramsan.edifikana.client.ui.resources

import edifikana_ui.Res
import edifikana_ui.L_Depa_city
import edifikana_ui.M_Depa_city
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
 * Usage in Compose:
 * ```
 * Image(
 *     painter = painterResource(PropertyIcons.CASA),
 *     contentDescription = "Casa"
 * )
 * ```
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
}
