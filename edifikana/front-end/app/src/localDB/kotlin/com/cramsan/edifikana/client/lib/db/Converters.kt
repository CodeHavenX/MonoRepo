package com.cramsan.edifikana.client.lib.db

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Type converters for Room.
 */
object Converters {
    private val json = Json

    /**
     * Convert a list of strings to a single string.
     */
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.let { json.decodeFromString<List<String>>(it) }.orEmpty()
    }

    /**
     * Convert a single string to a list of strings.
     */
    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.let { json.encodeToString(it) }.orEmpty()
    }
}
