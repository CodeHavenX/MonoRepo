package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.JvmDownloadStrategy
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.framework.assertlib.assertFalse
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        JvmDownloadStrategy()
    }

    single(named(EDIFIKANA_SUPABASE_URL)) {
        val enabledOverride = get<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED))
        val overrideUrl = get<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_URL))

        val supabaseUrl = System.getenv("EDIFIKANA_SUPABASE_URL")

        if (enabledOverride) {
            overrideUrl
        } else {
            assertFalse(supabaseUrl.isNullOrBlank(), TAG, "EDIFIKANA_SUPABASE_URL cannot be blank")
            supabaseUrl
        }
    }

    single(named(EDIFIKANA_SUPABASE_KEY)) {
        val enabledOverride = get<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED))
        val overrideKey = get<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_KEY))

        val supabaseKey = System.getenv("EDIFIKANA_SUPABASE_KEY")

        if (enabledOverride) {
            overrideKey
        } else {
            assertFalse(supabaseKey.isNullOrBlank(), TAG, "EDIFIKANA_SUPABASE_KEY cannot be blank")
            supabaseKey
        }
    }
}

private const val TAG = "ManagerPlatformModule"
