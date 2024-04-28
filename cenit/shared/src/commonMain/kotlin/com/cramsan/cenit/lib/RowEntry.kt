package com.cramsan.cenit.lib

data class RowEntry(
    val dateRecorded: String,
    val dateUploaded: String,
    val message: String,
) {
    fun toList(): List<String> {
        return listOf(
            dateRecorded,
            dateUploaded,
            message,
        )
    }
}
