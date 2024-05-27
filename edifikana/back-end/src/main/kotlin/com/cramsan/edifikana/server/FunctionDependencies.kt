package com.cramsan.edifikana.server

import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp

interface FunctionDependencies {
    val firebaseApp: FirebaseApp
    val firestore: Firestore
    val sheets: Sheets
    val drive: Drive
}
