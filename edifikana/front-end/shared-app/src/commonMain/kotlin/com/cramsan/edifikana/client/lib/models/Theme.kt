package com.cramsan.edifikana.client.lib.models

/**
 * Theme enum.
 */
enum class Theme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT,
    ;

    companion object {

        /**
         * Get the [Theme] from a string value.
         */
        fun fromString(value: String?): Theme = when (value?.lowercase()) {
            "light" -> LIGHT
            "dark" -> DARK
            "system_default" -> SYSTEM_DEFAULT
            else -> SYSTEM_DEFAULT
        }
    }
}
