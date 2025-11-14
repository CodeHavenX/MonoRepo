package com.cramsan.edifikana.client.lib.models

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT,
    ;
    companion object {
        fun fromString(value: String?): Theme {
            return when (value?.lowercase()) {
                "light" -> LIGHT
                "dark" -> DARK
                "system_default" -> SYSTEM_DEFAULT
                else -> SYSTEM_DEFAULT
            }
        }
    }
}