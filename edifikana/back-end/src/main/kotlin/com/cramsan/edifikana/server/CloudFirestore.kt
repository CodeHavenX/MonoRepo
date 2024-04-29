package com.cramsan.edifikana.server

import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream
import java.io.InputStream


class CloudFirestore {
    /**
     * Initializes the Cloud Firestore service with our firebase credentials
     */
    fun initializeCloudFirestore() {
        val credentialsPath: String = "/edifikana-firebase-adminsdk.json"
        val input: InputStream = object {}.javaClass.getResourceAsStream(credentialsPath) ?: TODO("ERROR: FILE IS MISSING!!")


        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(input))
            .build()

        FirebaseApp.initializeApp(options)
    }
}

fun main() {
    CloudFirestore().initializeCloudFirestore()
}
