package com.cramsan.edifikana.client.desktop.service

import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.models.PropertyConfigModel
import com.cramsan.edifikana.client.lib.service.PropertyConfigService
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.PropertyConfig
import com.cramsan.framework.logging.logI
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirebasePropertyConfigService(
    private val fireStore: FirebaseFirestore,
) : PropertyConfigService {

    @OptIn(FireStoreModel::class)
    override suspend fun getPropertyConfig(): Result<PropertyConfigModel> = runSuspendCatching {
        logI(TAG, "getPropertyConfig")
        fireStore.collection(PropertyConfig.COLLECTION)
            .document("cenit_01")
            .get()
            .data<PropertyConfig>()
            .toDomainModel()
    }

    companion object {
        private const val TAG = "FirebasePropertyConfigService"
    }
}
