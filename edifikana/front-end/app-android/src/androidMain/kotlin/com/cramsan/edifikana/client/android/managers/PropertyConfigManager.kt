package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.PropertyConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyConfigManager @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val workContext: WorkContext,
) {
    @OptIn(FireStoreModel::class)
    suspend fun getPropertyConfig(): Result<PropertyConfigModel> = workContext.getOrCatch(TAG) {
        fireStore.collection(PropertyConfig.COLLECTION)
            .document("cenit_01")
            .get()
            .await()
            .toObject(PropertyConfig::class.java)
            ?.toDomainModel() ?: throw RuntimeException("PropertyConfig not found")
    }

    companion object {
        const val TAG = "PropertyConfigManager"
    }
}
