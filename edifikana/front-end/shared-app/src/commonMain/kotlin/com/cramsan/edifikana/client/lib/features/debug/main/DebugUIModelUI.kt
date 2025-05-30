@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.debug.main

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Debug feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class DebugUIModelUI(
    val fields: List<Field>,
) : ViewModelUIState {
    companion object {
        val Initial = DebugUIModelUI(emptyList())
    }
}

/**
 * This is a field that represents a UI component that the user can interact with.
 */
sealed class Field {

    /**
     * This is a divider that separates different sections of the UI.
     */
    data object Divider : Field()

    /**
     * This field is a label used to provide some context or a title to a section.
     */
    data class Label(
        val label: String,
        val subtitle: String? = null,
    ) : Field()

    /**
     * This is a string field that represents a text input.
     */
    data class StringField(
        val title: String,
        val subtitle: String?,
        val key: String,
        val value: String,
        val secure: Boolean = false,
    ) : Field()

    /**
     * This is a boolean field that represents a boolean toggle.
     */
    data class BooleanField(
        val title: String,
        val subtitle: String?,
        val key: String,
        val value: Boolean,
        val enabled: Boolean = true,
    ) : Field()
}
