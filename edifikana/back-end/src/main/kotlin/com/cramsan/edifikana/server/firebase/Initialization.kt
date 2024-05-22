package com.cramsan.edifikana.server.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.InputStream

/**
 * Initializes the Firebase Firestore service with our firebase credentials
 */
fun initializeFirebase(credentials: GoogleCredentials): FirebaseApp {
    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(credentials)
        .setProjectId("edifikana")
        .build()

    return FirebaseApp.initializeApp(options)
}

/**
 * Initializes the Firebase Firestore service with our firebase credentials
 */
fun initializeFirestoreService(firebaseApp: FirebaseApp): Firestore {
    return FirestoreClient.getFirestore(firebaseApp)
}

/**
 * Loads the locally bundled credentials.
 */
fun getLocalFirebaseCredentials(): GoogleCredentials {
    // TODO: Identify the path to the credentials file at build time
    val credentialsPath = "/firebase-admin-sdk.json"
    val input: InputStream = object {}.javaClass.getResourceAsStream(credentialsPath) ?: TODO(
        "ERROR: FILE IS MISSING!!"
    )
    return GoogleCredentials.fromStream(input)
}
