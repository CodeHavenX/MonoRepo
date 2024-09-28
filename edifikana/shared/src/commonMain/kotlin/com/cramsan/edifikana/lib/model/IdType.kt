package com.cramsan.edifikana.lib.model

/**
 * Enum representing the type of ID.
 */
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
