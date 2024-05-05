package com.cramsan.edifikana.lib.firestore

enum class IdType {
    DNI,
    CE,
    PASSPORT,
    OTHER,
    ;
    companion object {
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