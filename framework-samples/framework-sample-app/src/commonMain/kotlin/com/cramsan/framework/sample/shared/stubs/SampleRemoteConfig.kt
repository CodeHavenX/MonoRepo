package com.cramsan.framework.sample.shared.stubs

import com.cramsan.framework.remoteconfig.RemoteConfig

/** Stub implementation of [RemoteConfig] backed by [SampleRemoteConfigPayload]. */
class SampleRemoteConfig : RemoteConfig<SampleRemoteConfigPayload> {
    private var payload: SampleRemoteConfigPayload? = null

    override fun isConfigPayloadReady() = payload != null

    override suspend fun downloadConfigPayload(): Boolean {
        payload = SampleRemoteConfigPayload(featureEnabled = true, configValue = "sample-value")
        return true
    }

    override fun downloadConfigPayloadAsync() {
        payload = SampleRemoteConfigPayload(featureEnabled = true, configValue = "async-sample-value")
    }

    override fun getConfigPayloadOrNull() = payload

    override fun getConfigPayloadOrDefault() =
        payload ?: SampleRemoteConfigPayload(featureEnabled = false, configValue = "default")
}
