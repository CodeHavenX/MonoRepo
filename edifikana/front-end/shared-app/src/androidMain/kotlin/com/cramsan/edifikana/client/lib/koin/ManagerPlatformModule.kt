package com.cramsan.edifikana.client.lib.koin

import com.cramsan.edifikana.client.lib.BuildConfig
import com.cramsan.edifikana.client.lib.di.koin.EDIFIKANA_SUPABASE_KEY
import com.cramsan.edifikana.client.lib.di.koin.EDIFIKANA_SUPABASE_URL
import com.cramsan.edifikana.client.lib.service.AndroidDownloadStrategy
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.framework.assertlib.assertFalse
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        AndroidDownloadStrategy(get())
    }

    single(named(EDIFIKANA_SUPABASE_URL)) {
        val enabledOverride = get<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED))
        val overrideUrl = get<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_URL))

        val supabaseUrl = BuildConfig.EDIFIKANA_SUPABASE_URL
        assertFalse(supabaseUrl.isBlank(), TAG, "EDIFIKANA_SUPABASE_URL cannot be blank")

        if (enabledOverride && overrideUrl.isNotBlank()) {
            overrideUrl
        } else {
            supabaseUrl
        }
    }

    single(named(EDIFIKANA_SUPABASE_KEY)) {
        val enabledOverride = get<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED))
        val overrideKey = get<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_KEY))

        val supabaseKey = BuildConfig.EDIFIKANA_SUPABASE_KEY
        assertFalse(supabaseKey.isBlank(), TAG, "EDIFIKANA_SUPABASE_KEY cannot be blank")

        if (enabledOverride && overrideKey.isNotBlank()) {
            overrideKey
        } else {
            supabaseKey
        }
    }
}

private const val TAG = "ManagerPlatformModule"
