package com.cramsan.edifikana.lib.model.identification

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Enum representing the type of ID.
 */
@Serializable
@JsonSchema.Description("Type of government or personal identification document.")
enum class IdType {
    DNI,
    CE,
    PASSPORT,
    OTHER,
    ;

    companion object {
        /**
         * Converts a string to an IdType.
         */
        fun fromString(value: String): IdType {
            return when (value) {
                "DNI" -> DNI
                "CE" -> CE
                "PASSPORT" -> PASSPORT
                "OTHER" -> OTHER
                else -> throw IllegalArgumentException("Invalid IdType value: $value")
            }
        }
    }
}
