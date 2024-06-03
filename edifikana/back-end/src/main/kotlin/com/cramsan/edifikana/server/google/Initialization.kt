package com.cramsan.edifikana.server.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials

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
fun getCredentials(credentialsPath: String, scopes: Collection<String>): GoogleCredentials {
    // Load service account key.
    val input = object {}.javaClass.getResourceAsStream(credentialsPath) ?: TODO()
    return ServiceAccountCredentials.fromStream(input).createScoped(scopes)
}

/**
 * Loads the locally bundled credentials.
 */
fun getLocalDriveCredentials(): GoogleCredentials {
    return getCredentials(CREDENTIALS_FILE_PATH, SCOPES)
}

/**
 * Global constants
 */
const val APPLICATION_NAME = "Google API Java Quickstart"
val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

val SCOPES = listOf(
    SheetsScopes.DRIVE,
    SheetsScopes.SPREADSHEETS,
)
const val CREDENTIALS_FILE_PATH = "/.secrets/gdrive-access-service.json"
