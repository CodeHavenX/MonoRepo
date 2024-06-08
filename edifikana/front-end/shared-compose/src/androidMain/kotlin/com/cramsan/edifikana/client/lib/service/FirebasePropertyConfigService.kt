package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.PropertyConfig
import com.cramsan.framework.logging.logI
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebasePropertyConfigService(
    private val fireStore: FirebaseFirestore,
) : PropertyConfigService {

    @OptIn(FireStoreModel::class)
    override suspend fun getPropertyConfig(): Result<PropertyConfigModel> = runSuspendCatching {
        logI(TAG, "getPropertyConfig")
        fireStore.collection(PropertyConfig.COLLECTION)
            .document("cenit_01")
            .get()
            .await()
            .toObject(PropertyConfig::class.java)
            ?.toDomainModel() ?: throw RuntimeException("PropertyConfig not found")
    }

    companion object {
        private const val TAG = "FirebasePropertyConfigService"
    }
}
