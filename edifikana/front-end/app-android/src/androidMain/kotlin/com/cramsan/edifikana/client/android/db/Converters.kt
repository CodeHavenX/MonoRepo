package com.cramsan.edifikana.client.android.db

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converters {
    private val json = Json

    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.let { json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.let { json.encodeToString(it) }.orEmpty()
    }
}
