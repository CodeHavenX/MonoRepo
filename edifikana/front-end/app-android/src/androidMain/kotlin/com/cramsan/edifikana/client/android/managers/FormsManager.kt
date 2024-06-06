package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.client.android.managers.AuthManager.Companion.TAG
import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.Form
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecord
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class FormsManager @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val workContext: WorkContext,
) {
    @OptIn(FireStoreModel::class)
    suspend fun getForms(): Result<List<FormModel>> = workContext.getOrCatch(TAG) {
        fireStore.collection(Form.COLLECTION)
            .whereEqualTo("propertyId", "cenit_01")
            .get()
            .await()
            .toObjects(Form::class.java)
            .toList()
            .map { it.toDomainModel() }
    }

    @OptIn(FireStoreModel::class)
    suspend fun getForm(formPK: FormPK): Result<FormModel> = workContext.getOrCatch(TAG) {
        fireStore.collection(Form.COLLECTION)
            .document(formPK.documentPath)
            .get()
            .await()
            .toObject(Form::class.java)
            ?.toDomainModel() ?: throw RuntimeException("Form $formPK not found")
    }

    @OptIn(FireStoreModel::class)
    suspend fun getFormRecords(): Result<List<FormRecordModel>> = workContext.getOrCatch(TAG) {
        // TODO: Make this range configurable
        val now = workContext.clock.now()
        val fourDaysAgo = now.minus(4.days).epochSeconds

        fireStore.collection(FormRecord.COLLECTION)
            .whereEqualTo("propertyId", "cenit_01")
            .whereGreaterThan("timeRecorded", fourDaysAgo)
            .get()
            .await()
            .toObjects(FormRecord::class.java)
            .toList()
            .map { it.toDomainModel() }
            .sortedByDescending { it.timeRecorded }
    }

    @OptIn(FireStoreModel::class)
    suspend fun getFormRecord(formRecordPK: FormRecordPK): Result<FormRecordModel> = workContext.getOrCatch(TAG) {
        fireStore.collection(FormRecord.COLLECTION)
            .document(formRecordPK.documentPath)
            .get()
            .await()
            .toObject(FormRecord::class.java)
            ?.toDomainModel() ?: throw RuntimeException("FormRecord $formRecordPK not found")
    }

    @OptIn(FireStoreModel::class)
    suspend fun submitFormRecord(formRecordModel: FormRecordModel): Result<Unit> = workContext.getOrCatch(TAG) {
        val firebaseFormRecord = formRecordModel.toFirebaseModel("cenit_01")
        fireStore.collection(FormRecord.COLLECTION)
            .document(firebaseFormRecord.formRecordPK().documentPath)
            .set(firebaseFormRecord)
            .await()
    }
}
