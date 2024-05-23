package com.cramsan.edifikana.server

/**
 * Parameters needed to do operations on Google Drive.
 *
 * @param storageFolderId The ID of the folder where the files will be stored
 * @param timeCardSpreadsheetId The ID of the time card spreadsheet
 * @param eventLogSpreadsheetId The ID of the event log spreadsheet
 * @param formEntriesSpreadsheetId The ID of the form entries spreadsheet
 */
data class GoogleDriveParameters(
    val storageFolderId: String,
    val timeCardSpreadsheetId: String,
    val eventLogSpreadsheetId: String,
    val formEntriesSpreadsheetId: String,
)
