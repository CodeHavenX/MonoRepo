package com.cramsan.edifikana.client.android.managers.remoteconfig

import com.cramsan.edifikana.client.lib.managers.remoteconfig.BehaviorConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.CachingConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.FeatureConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.Features
import com.cramsan.edifikana.client.lib.managers.remoteconfig.ImageConfig
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.logging.logI
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

class RemoteConfigService @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) {
    fun getRemoteConfigPayload(): RemoteConfig {
        val config = RemoteConfig(
            image = ImageConfig(
                captureWidth = remoteConfig.getLong("captureWidth").toInt(),
                captureHeight = remoteConfig.getLong("captureHeight").toInt(),
            ),
            caching = CachingConfig(
                imageQualityHint = remoteConfig.getLong("imageQualityHint").toInt(),
            ),
            behavior = BehaviorConfig(
                fetchPeriod = remoteConfig.getLong("fetchPeriod").toInt(),
                allowListedCodes = remoteConfig.getString("allowListedCodes")
                    .split(",")
                    .map { it.trim() }
            ),
            features = FeatureConfig(
                flags = Features.entries.associateWith { remoteConfig.getBoolean(it.key) }
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
