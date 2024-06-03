package com.cramsan.edifikana.server.drive

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.util.*

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

fun createFolder(
    drive: Drive,
    folderId: String,
    folderName: String,
): String {
    // File's metadata.
    val fileMetadata = File()
    fileMetadata.setName(folderName)
    fileMetadata.setParents(Collections.singletonList(folderId))
    fileMetadata.setMimeType("application/vnd.google-apps.folder")
    try {
        val file = drive.files().create(fileMetadata)
            .setFields("id")
            .execute()
        println("Folder ID: " + file.id)
        return file.id
    } catch (e: GoogleJsonResponseException) {
        System.err.println("Unable to create folder: " + e.details)
        throw e
    }
}

fun createSpreadsheet(
    drive: Drive,
    folderId: String,
    spreadsheetName: String,
): String {
    // File's metadata.
    val fileMetadata = File()
    fileMetadata.setName(spreadsheetName)
    fileMetadata.setParents(Collections.singletonList(folderId))
    fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet")
    try {
        val file = drive.files().create(fileMetadata)
            .setFields("id")
            .execute()
        println("Spreadsheet ID: " + file.id)
        return file.id
    } catch (e: GoogleJsonResponseException) {
        System.err.println("Unable to create spreadsheet: " + e.details)
        throw e
    }
}
