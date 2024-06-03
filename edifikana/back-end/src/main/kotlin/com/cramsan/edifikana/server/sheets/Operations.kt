package com.cramsan.edifikana.server.sheets

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.AddSheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import java.util.*

/**
 * Appends the specified values to the specified range in the spreadsheet
 *
 * @param sheets The Google Sheets API service
 * @param spreadsheetId The ID of the spreadsheet
 * @param range The range to append the values to
 * @param values The values to append
 * @throws GoogleJsonResponseException If an error occurs while appending the values
 */
fun appendValues(
    sheets: Sheets,
    spreadsheetId: String?,
    sheetName: String,
    values: List<List<String>>,
) {
    try {
        // Append values to the specified range.
        val range = "$sheetName!A1"
        val sanitizedValues = values.map { row -> row.map { it.ifEmpty { "/" } } }
        val body = ValueRange().setValues(sanitizedValues)
        val result = sheets.spreadsheets().values()
            .append(spreadsheetId, range, body)
            .setValueInputOption(ValueInputOption.USER_ENTERED.name)
            .setInsertDataOption("INSERT_ROWS")
            .execute()
        println(result)
    } catch (e: GoogleJsonResponseException) {
        System.err.println(e.details.code)
        throw e
    }
}

fun createSheetTab(
    sheets: Sheets,
    spreadsheetId: String?,
    sheetName: String,
) {
    try {
        // Create a new AddSheetRequest
        val addSheetRequest = AddSheetRequest().setProperties(SheetProperties().setTitle(sheetName))

        // Create batch update request
        val batchUpdateRequest = BatchUpdateSpreadsheetRequest()
            .setRequests(Collections.singletonList(Request().setAddSheet(addSheetRequest)))

        // Call the Sheets API to execute the batch update
        val response = sheets.spreadsheets()
            .batchUpdate(spreadsheetId, batchUpdateRequest)
            .execute()

        val newSheetId = response.replies[0].addSheet.properties.sheetId
        println("Sheet ID: $newSheetId")
    } catch (e: GoogleJsonResponseException) {
        System.err.println(e.details.code)
        throw e
    }
}

fun checkIfSheetExists(
    sheetsService: Sheets,
    spreadsheetId: String,
    sheetName: String,
): Boolean {
    // Get the spreadsheet
    val spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute()

    // Get the list of sheets in the spreadsheet
    val sheetList = spreadsheet.sheets

    // Check if any of the sheets have the desired name
    return sheetList.any { it.properties.title == sheetName }
}

/**
 * ValueInputOption enum
 *
 * https://developers.google.com/sheets/api/reference/rest/v4/ValueInputOption
 */
private enum class ValueInputOption {
    // The values the user has entered will not be parsed and will be stored as-is.
    RAW,

    // The values will be parsed as if the user typed them into the UI.
    // Numbers will stay as numbers, but strings may be converted to numbers, dates, etc. following the same
    // rules that are applied when entering text into a cell via the Google Sheets UI.
    USER_ENTERED,
}
