package com.cramsan.edifikana.client.lib.features.root.debug.main

/**
 * Debug UI model. You can use one or multiple models to represent
 * specific sections and component of the UI.
 */
data class DebugUIModel(
    val fields: List<Field>,
)

/**
 * This is a field that represents a UI component that the user can interact with.
 */
sealed class Field {

    /**
     * This is a divider that separates different sections of the UI.
     */
    data object Divider : Field()

    /**
     * This is a string field that represents a text input.
     */
    data class StringField(
        val key: String,
        val value: String,
    ) : Field()

    /**
     * This is a boolean field that represents a boolean toggle.
     */
    data class BooleanField(
        val key: String,
        val value: Boolean,
    ) : Field()
}
