package com.cramsan.edifikana.server.drive

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import java.util.Collections

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
            .setValueInputOption(ValueInputOption.RAW.name)
            .setInsertDataOption("INSERT_ROWS")
            .execute()
        println(result)
    } catch (e: GoogleJsonResponseException) {
        System.err.println(e.details.code)
        throw e
    }
}

/**
 * Uploads the specified file to the specified folder
 *
 * @param drive The Google Drive API service
 * @param folderId The ID of the folder
 * @param sourceFilePath The path to the file to upload
 * @param sourceFileType The MIME type of the file
 * @param uploadedFileName The name of the file once uploaded
 * @throws GoogleJsonResponseException If an error occurs while uploading the file
 */
fun uploadFile(
    drive: Drive,
    folderId: String,
    sourceFilePath: String,
    sourceFileType: String,
    uploadedFileName: String,
): String {
    // Configure the settings for the file once uploaded.
    val fileMetadata = File()
    // Set the final name
    fileMetadata.setName(uploadedFileName)
    // Set the parent folder.
    fileMetadata.setParents(Collections.singletonList(folderId))

    // File's content.
    val filePath = java.io.File(sourceFilePath)
    // Specify media type. https://developers.google.com/drive/api/guides/mime-types
    val mediaContent = FileContent(sourceFileType, filePath)
    try {
        val file: File = drive.files().create(fileMetadata, mediaContent)
            // This line specifies that the response from the create method
            // should include the ID of the newly created file.
            .setFields("id")
            .execute()
        println("File ID: " + file.id)
        return "https://drive.google.com/file/d/${file.id}/view"
    } catch (e: GoogleJsonResponseException) {
        System.err.println("Unable to upload file: " + e.details)
        throw e
    }
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
