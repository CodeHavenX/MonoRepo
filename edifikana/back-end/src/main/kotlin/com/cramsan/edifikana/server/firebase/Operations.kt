package com.cramsan.edifikana.server.firebase

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.Form
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecord
import com.cramsan.edifikana.lib.firestore.PropertyConfig
import com.cramsan.edifikana.lib.firestore.PropertyConfigPK
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.cramsan.edifikana.server.models.toEmployee
import com.cramsan.edifikana.server.models.toFormRecord
import com.cramsan.edifikana.server.models.toPropertyConfig
import com.cramsan.edifikana.server.models.toTimeCardEvent
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
@Deprecated("Use the type safe version instead")
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

    require(document.exists()) { "No such document!" }
    return document
}

internal fun setDocument(
    firestore: Firestore,
    collection: String,
    documentId: String,
    document: Any,
) {
    val docRef: DocumentReference = firestore
        .collection(collection)
        .document(documentId)
    val addRef = docRef.set(document)
    // Get a future that represents the work to get the document
    addRef.get()
}

@FireStoreModel
fun getEmployee(
    firestore: Firestore,
    employeePK: EmployeePK,
): Employee {
    return getDocument(firestore, Employee.COLLECTION, employeePK.documentPath).toEmployee()
}

@FireStoreModel
fun getTimeCardRecord(
    firestore: Firestore,
    timeCardRecordPK: TimeCardRecordPK,
): TimeCardRecord {
    return getDocument(firestore, TimeCardRecord.COLLECTION, timeCardRecordPK.documentPath).toTimeCardEvent()
}

@FireStoreModel
fun getForms(
    firestore: Firestore,
    formPK: FormPK,
): FormRecord {
    return getDocument(firestore, Form.COLLECTION, formPK.documentPath).toFormRecord()
}

@FireStoreModel
fun getPropertyConfig(
    firestore: Firestore,
    propertyConfigPK: PropertyConfigPK,
): PropertyConfig {
    return getDocument(firestore, PropertyConfig.COLLECTION, propertyConfigPK.documentPath).toPropertyConfig()
}

@FireStoreModel
fun updatePropertyConfig(
    firestore: Firestore,
    propertyConfig: PropertyConfig,
) {
    return setDocument(
        firestore,
        PropertyConfig.COLLECTION,
        propertyConfig.documentId().documentPath,
        propertyConfig,
    )
}
