package com.cramsan.edifikana.server.drive

import com.cramsan.edifikana.server.google.APPLICATION_NAME
import com.cramsan.edifikana.server.google.JSON_FACTORY
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.services.drive.Drive

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
