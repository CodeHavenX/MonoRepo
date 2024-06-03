package com.cramsan.edifikana.server.sheets

import com.cramsan.edifikana.server.google.APPLICATION_NAME
import com.cramsan.edifikana.server.google.JSON_FACTORY
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.services.sheets.v4.Sheets

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
