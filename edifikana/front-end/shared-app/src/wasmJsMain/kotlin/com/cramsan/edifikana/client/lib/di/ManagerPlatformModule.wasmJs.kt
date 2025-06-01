package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.WasmDownloadStrategy
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.client.lib.utils.IODependencies
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        WasmDownloadStrategy()
    }

    single(named(EDIFIKANA_SUPABASE_URL)) {
        val enabledOverride = get<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED))
        val overrideUrl = get<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_URL))

        if (enabledOverride && overrideUrl.isNotBlank()) {
            overrideUrl
        } else {
            ""
        }
    }

    single(named(EDIFIKANA_SUPABASE_KEY)) {
        val enabledOverride = get<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED))
        val overrideKey = get<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_KEY))

        if (enabledOverride && overrideKey.isNotBlank()) {
            overrideKey
        } else {
            ""
        }
    }
}
