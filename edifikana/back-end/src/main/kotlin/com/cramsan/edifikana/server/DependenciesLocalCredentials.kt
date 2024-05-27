package com.cramsan.edifikana.server

import com.cramsan.edifikana.server.drive.getHttpTransport
import com.cramsan.edifikana.server.drive.getLocalDriveCredentials
import com.cramsan.edifikana.server.drive.getRequestInitializer
import com.cramsan.edifikana.server.drive.initializeDriveService
import com.cramsan.edifikana.server.drive.initializeSpreadsheetService
import com.cramsan.edifikana.server.firebase.getLocalFirebaseCredentials
import com.cramsan.edifikana.server.firebase.initializeFirebase
import com.cramsan.edifikana.server.firebase.initializeFirestoreService
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp

class DependenciesLocalCredentials : FunctionDependencies {

    override val firebaseApp: FirebaseApp

    override val firestore: Firestore

    override val sheets: Sheets

    override val drive: Drive

    init {
        val googleCredentials = getLocalFirebaseCredentials()
        firebaseApp = initializeFirebase(googleCredentials)
        firestore = initializeFirestoreService(firebaseApp)
        val credentials = getLocalDriveCredentials()
        val httpTransport = getHttpTransport()
        val requestInitializer = getRequestInitializer(credentials)
        sheets = initializeSpreadsheetService(httpTransport, requestInitializer)
        drive = initializeDriveService(httpTransport, requestInitializer)
    }
}
