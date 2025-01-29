package com.cramsan.edifikana.client.lib.koin

import com.cramsan.edifikana.client.lib.di.koin.EDIFIKANA_SUPABASE_KEY
import com.cramsan.edifikana.client.lib.di.koin.EDIFIKANA_SUPABASE_URL
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.WasmDownloadStrategy
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.framework.assertlib.assertFalse
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        WasmDownloadStrategy()
    }

    single(named(EDIFIKANA_SUPABASE_URL)) {
        ""
    }

    single(named(EDIFIKANA_SUPABASE_KEY)) {
        ""
    }
}

private const val TAG = "ManagerPlatformModule"
