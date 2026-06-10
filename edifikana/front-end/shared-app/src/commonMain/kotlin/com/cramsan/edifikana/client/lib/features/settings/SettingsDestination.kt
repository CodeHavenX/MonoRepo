package com.cramsan.edifikana.client.lib.features.settings

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.WebDestination
import com.cramsan.framework.core.compose.navigation.toWebPathIfRoute
import com.cramsan.framework.core.compose.navigation.webRoute
import kotlinx.serialization.Serializable

/**
 * Destinations in the Settings graph.
 */
@Serializable
sealed class SettingsDestination : WebDestination {
    /** General app settings screen destination. */
    @Serializable
    data object GeneralSettingsDestination : SettingsDestination()

    override fun toWebPath(): String =
        when (this) {
            is GeneralSettingsDestination -> Companion.generalSettingsRoute.toWebPath(this)
        }

    companion object {
        private val generalSettingsRoute by lazy { webRoute<GeneralSettingsDestination>("/settings") }

        /** Parses [path] and returns the matching [SettingsDestination], or null if unrecognised. */
        fun fromWebPath(path: String): SettingsDestination? =
            generalSettingsRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<GeneralSettingsDestination>()
    }
}
