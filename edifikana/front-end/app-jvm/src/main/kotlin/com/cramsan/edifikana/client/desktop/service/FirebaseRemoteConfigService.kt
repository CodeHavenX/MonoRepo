package com.cramsan.edifikana.client.desktop.service

import com.cramsan.edifikana.client.lib.managers.remoteconfig.BehaviorConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.CachingConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.FeatureConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.Features
import com.cramsan.edifikana.client.lib.managers.remoteconfig.ImageConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.edifikana.client.lib.service.RemoteConfigService
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.logging.logI
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get

class FirebaseRemoteConfigService(
    private val remoteConfig: FirebaseRemoteConfig,
) : RemoteConfigService {
    override fun getRemoteConfigPayload(): RemoteConfig {
        val config = RemoteConfig(
            image = ImageConfig(
                captureWidth = remoteConfig["captureWidth"],
                captureHeight = remoteConfig["captureHeight"],
            ),
            caching = CachingConfig(
                imageQualityHint = remoteConfig["imageQualityHint"],
            ),
            behavior = BehaviorConfig(
                fetchPeriod = remoteConfig["fetchPeriod"],
                allowListedCodes = remoteConfig.get<String>("allowListedCodes")
                    .split(",")
                    .map { it.trim() }
            ),
            features = FeatureConfig(
                flags = Features.entries.associateWith { remoteConfig[it.key] }
            )
        )

        logI(TAG, "Remote config payload: $config")

        verifyConfigIntegrity(config)

        return config
    }

    @Suppress("MagicNumber")
    private fun verifyConfigIntegrity(config: RemoteConfig) {
        assert(config.image.captureWidth > 0, TAG, "Capture width must be greater than 0")
        assert(config.image.captureHeight > 0, TAG, "Capture height must be greater than 0")
        assert(config.caching.imageQualityHint in 10..100, TAG, "Image quality hint must be between 10 and 100")
        assert(config.behavior.fetchPeriod > 0, TAG, "Fetch period must be greater than 0")
        assert(config.behavior.allowListedCodes.isNotEmpty(), TAG, "Allow listed codes must not be empty")
    }

    companion object {
        private const val TAG = "RemoteConfigService"
    }
}
