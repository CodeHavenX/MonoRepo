package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig

interface RemoteConfigService {
    fun getRemoteConfigPayload(): RemoteConfig
}
