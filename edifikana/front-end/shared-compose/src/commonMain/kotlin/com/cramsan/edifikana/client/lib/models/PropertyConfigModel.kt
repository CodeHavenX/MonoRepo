package com.cramsan.edifikana.client.lib.models

import kotlinx.datetime.TimeZone

/**
 * Model for a property configuration.
 */
data class PropertyConfigModel(
    val propertyId: String,
    val driveFolderId: String,
    val storageFolderId: String,
    val timeCardSpreadsheetId: String,
    val eventLogSpreadsheetId: String,
    val formEntriesSpreadsheetId: String,
    val timeZone: TimeZone,
) {
    /**
     * Get the URL for the driver folder.
     */
    fun driverFolderUrl(): String {
        return "https://drive.google.com/drive/folders/$driveFolderId"
    }

    /**
     * Get the URL for the storage folder.
     */
    fun timeCartSpreadsheetUrl(): String {
        return "https://docs.google.com/spreadsheets/d/$timeCardSpreadsheetId"
    }

    /**
     * Get the URL for the event log spreadsheet.
     */
    fun eventLogSpreadsheetUrl(): String {
        return "https://docs.google.com/spreadsheets/d/$eventLogSpreadsheetId"
    }

    /**
     * Get the URL for the form entries spreadsheet.
     */
    fun formEntriesSpreadsheetUrl(): String {
        return "https://docs.google.com/spreadsheets/d/$formEntriesSpreadsheetId"
    }
}
