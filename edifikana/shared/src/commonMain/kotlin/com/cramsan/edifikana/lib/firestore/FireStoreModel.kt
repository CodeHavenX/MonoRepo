package com.cramsan.edifikana.lib.firestore

@RequiresOptIn(message = "This class should only be used for Firestore models.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class FireStoreModel
