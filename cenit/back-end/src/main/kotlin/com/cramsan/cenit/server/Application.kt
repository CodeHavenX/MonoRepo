package com.cramsan.cenit.server

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import java.util.Collections

fun main() {
    val credentials = getCredentials(CREDENTIALS_FILE_PATH, SCOPES)
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

    val sheets = initializeSpreadsheetService(httpTransport, requestInitializer)
    val drive = initializeDriveService(httpTransport, requestInitializer)

    uploadFile(
        drive,
        FOLDER,
        "local.properties",
        "text/plain",
        "test.txt",
    )
    appendValues(
        sheets,
        SPREADSHEET_ID,
        "Sheet1!A1",
        listOf(
            listOf("Hello, world!")
        )
    )
}

fun getCredentials(credentialsPath: String, scopes: Collection<String>): GoogleCredentials {
    // Load service account key.
    val input = object {}.javaClass.getResourceAsStream(credentialsPath) ?: TODO()
    return ServiceAccountCredentials.fromStream(input).createScoped(scopes)
}

fun initializeSpreadsheetService(httpTransport: HttpTransport, requestInitializer: HttpRequestInitializer): Sheets {
    return Sheets.Builder(httpTransport, JSON_FACTORY, requestInitializer)
        .setApplicationName(APPLICATION_NAME)
        .build()
}

fun initializeDriveService(httpTransport: HttpTransport, requestInitializer: HttpRequestInitializer): Drive {
    // Build a new authorized API client service.
    return Drive.Builder(httpTransport, JSON_FACTORY, requestInitializer)
        .setApplicationName(APPLICATION_NAME)
        .build()
}

fun appendValues(
    sheets: Sheets,
    spreadsheetId: String?,
    range: String?,
    values: List<List<Any>>,
) {
    try {
        // Append values to the specified range.
        val body = ValueRange().setValues(values)
        val result = sheets.spreadsheets().values()
            .append(spreadsheetId, range, body)
            .setValueInputOption(ValueInputOption.RAW.name)
            .execute()
        println(result)
    } catch (e: GoogleJsonResponseException) {
        System.err.println(e.details.code)
        throw e
    }
}

fun uploadFile(
    drive: Drive,
    folderId: String,
    sourceFilePath: String,
    sourceFileType: String,
    uploadedFileName: String,
) {
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
    } catch (e: GoogleJsonResponseException) {
        System.err.println("Unable to upload file: " + e.details)
        throw e
    }
}

private const val APPLICATION_NAME = "Google API Java Quickstart"
private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

private val SCOPES = listOf(
    SheetsScopes.DRIVE,
    SheetsScopes.SPREADSHEETS,
)
private const val CREDENTIALS_FILE_PATH = "/credentials.json"

private const val SPREADSHEET_ID = "1mDgyQtJV_EkCrikM5-lBufwmFGS5N8FxMUVYvdbXSuM"
private const val FOLDER = "1ZRI7dP2X7VqwixGKz3OPJ6KB-uuDkkM1"

// https://developers.google.com/sheets/api/reference/rest/v4/ValueInputOption
enum class ValueInputOption {
    // The values the user has entered will not be parsed and will be stored as-is.
    RAW,
    // The values will be parsed as if the user typed them into the UI.
    // Numbers will stay as numbers, but strings may be converted to numbers, dates, etc. following the same
    // rules that are applied when entering text into a cell via the Google Sheets UI.
    USER_ENTERED,
}
