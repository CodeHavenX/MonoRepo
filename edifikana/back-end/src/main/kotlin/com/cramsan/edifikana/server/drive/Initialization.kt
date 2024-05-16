package com.cramsan.edifikana.server.drive

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials

/**
 * Loads the locally bundled credentials.
 */
fun getLocalDriveCredentials(): GoogleCredentials {
    return getCredentials(CREDENTIALS_FILE_PATH, SCOPES)
}

/**
 * Gets the HTTP transport
 */
fun getHttpTransport(): HttpTransport {
    return GoogleNetHttpTransport.newTrustedTransport()
}

/**
 * Gets the request initializer
 *
 * @param googleCredentials The Google credentials
 * @return The request initializer
 */
fun getRequestInitializer(googleCredentials: GoogleCredentials): HttpRequestInitializer {
    return HttpCredentialsAdapter(googleCredentials)
}

/**
 * Loads the credentials from the specified path and scopes
 *
 * @param credentialsPath The path to the credentials file
 * @param scopes The scopes to request
 * @return The loaded credentials
 */
private fun getCredentials(credentialsPath: String, scopes: Collection<String>): GoogleCredentials {
    // Load service account key.
    val input = object {}.javaClass.getResourceAsStream(credentialsPath) ?: TODO()
    return ServiceAccountCredentials.fromStream(input).createScoped(scopes)
}

/**
 * Initializes the Google Sheets API service
 *
 * @param httpTransport The HTTP transport
 * @param requestInitializer The request initializer
 * @return The Google Sheets API service
 */
fun initializeSpreadsheetService(httpTransport: HttpTransport, requestInitializer: HttpRequestInitializer): Sheets {
    return Sheets.Builder(httpTransport, JSON_FACTORY, requestInitializer)
        .setApplicationName(APPLICATION_NAME)
        .build()
}

/**
 * Initializes the Google Drive API service
 *
 * @param httpTransport The HTTP transport
 * @param requestInitializer The request initializer
 * @return The Google Drive API service
 */
fun initializeDriveService(httpTransport: HttpTransport, requestInitializer: HttpRequestInitializer): Drive {
    // Build a new authorized API client service.
    return Drive.Builder(httpTransport, JSON_FACTORY, requestInitializer)
        .setApplicationName(APPLICATION_NAME)
        .build()
}

/**
 * Global constants
 */
private const val APPLICATION_NAME = "Google API Java Quickstart"
private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

private val SCOPES = listOf(
    SheetsScopes.DRIVE,
    SheetsScopes.SPREADSHEETS,
)
private const val CREDENTIALS_FILE_PATH = "/gdrivde-credentials.json"
