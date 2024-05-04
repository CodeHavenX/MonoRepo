package com.cramsan.edifikana.server

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.InputStream


class CloudFirestore {
    // Global Static Variables
    companion object {
        const val credentialsPath: String = "/edifikana-firebase-adminsdk.json"
        val input: InputStream = object {}.javaClass.getResourceAsStream(credentialsPath) ?: TODO(
            "ERROR: FILE IS MISSING!!"
        )
    }

    /**
     * Initializes the Cloud Firestore service with our firebase credentials
     */
    fun initializeCloudFirestore() {
        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(input))
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun initializeGoogleFirestore(): Firestore {
        val firestoreOptions =
            FirestoreOptions.getDefaultInstance().toBuilder()
                .setProjectId("edifikana")
                .setCredentials(GoogleCredentials.fromStream(input))
                .build()

        return firestoreOptions.service
    }
}

fun main() {
    CloudFirestore().initializeCloudFirestore()
    val db: Firestore = FirestoreClient.getFirestore()

    val cloudFireAPI = CloudFireController(db)
//    CloudFirestore().initializeGoogleFirestore()
    cloudFireAPI.getEvent()
}
