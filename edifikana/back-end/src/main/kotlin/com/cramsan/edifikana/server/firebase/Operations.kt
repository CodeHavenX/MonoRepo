package com.cramsan.edifikana.server.firebase

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore

/**
 * Get a document from a Firestore collection
 *
 * @param firestore The Firestore instance
 * @param collection The collection name
 * @param documentId The document ID
 * @return The document snapshot
 */
fun getDocument(
    firestore: Firestore,
    collection: String,
    documentId: String,
): DocumentSnapshot {
    val docRef: DocumentReference = firestore
        .collection(collection)
        .document(documentId)
    // Get a future that represents the work to get the document
    val future = docRef.get()
    // Make a blocking call to get the document
    val document = future.get()

    if (!document.exists()) {
        throw IllegalArgumentException("No such document!")
    }
    return document
}
