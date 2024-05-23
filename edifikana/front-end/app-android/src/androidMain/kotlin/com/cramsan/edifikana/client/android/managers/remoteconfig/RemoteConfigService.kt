package com.cramsan.edifikana.client.android.managers.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

class RemoteConfigService @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) {
    fun isFeatureEnabled(feature: Features): Boolean {
        return remoteConfig.getBoolean(feature.key)
    }
}