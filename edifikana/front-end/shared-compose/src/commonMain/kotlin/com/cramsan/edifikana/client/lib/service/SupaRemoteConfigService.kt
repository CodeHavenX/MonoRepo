package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.remoteconfig.BehaviorConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.CachingConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.FeatureConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.ImageConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.framework.logging.logI

class SupaRemoteConfigService : RemoteConfigService {
    override fun getRemoteConfigPayload(): RemoteConfig {
        val config = RemoteConfig(
            image = ImageConfig(
                captureWidth = 1080,
                captureHeight = 1920,
            ),
            caching = CachingConfig(
                imageQualityHint = 30,
            ),
            behavior = BehaviorConfig(
                fetchPeriod = 3,
                allowListedCodes = listOf(),
            ),
            features = FeatureConfig(
                flags = mapOf(),
            )
        )

        logI(TAG, "Remote config payload: $config")
        return config
    }

    companion object {
        private const val TAG = "SupaRemoteConfigService"
    }
}
