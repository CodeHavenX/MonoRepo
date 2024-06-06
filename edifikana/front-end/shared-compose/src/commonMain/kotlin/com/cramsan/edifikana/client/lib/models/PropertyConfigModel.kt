package com.cramsan.edifikana.client.lib.models

import kotlinx.datetime.TimeZone

data class PropertyConfigModel(
    val propertyId: String,
    val driveFolderId: String,
    val storageFolderId: String,
    val timeCardSpreadsheetId: String,
    val eventLogSpreadsheetId: String,
    val formEntriesSpreadsheetId: String,
    val timeZone: TimeZone,
) {
    fun driverFolderUrl(): String {
        return "https://drive.google.com/drive/folders/$driveFolderId"
    }

    fun timeCartSpreadsheetUrl(): String {
        return "https://docs.google.com/spreadsheets/d/$timeCardSpreadsheetId"
    }

    fun eventLogSpreadsheetUrl(): String {
        return "https://docs.google.com/spreadsheets/d/$eventLogSpreadsheetId"
    }

    fun formEntriesSpreadsheetUrl(): String {
        return "https://docs.google.com/spreadsheets/d/$formEntriesSpreadsheetId"
    }
}
