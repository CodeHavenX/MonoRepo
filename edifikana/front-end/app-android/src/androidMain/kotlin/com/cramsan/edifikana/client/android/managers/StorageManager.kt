package com.cramsan.edifikana.client.android.managers

import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class StorageManager {

    fun uploadFile() {
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("mountains.jpg")
    }
}