package com.cramsan.edifikana.client.lib.koin

import com.cramsan.edifikana.client.lib.di.koin.EDIFIKANA_SUPABASE_KEY
import com.cramsan.edifikana.client.lib.di.koin.EDIFIKANA_SUPABASE_URL
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.JvmDownloadStrategy
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.framework.assertlib.assertFalse
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        JvmDownloadStrategy()
    }

    single(named(EDIFIKANA_SUPABASE_URL)) {
        val supabaseUrl = System.getenv("EDIFIKANA_SUPABASE_URL")
        assertFalse(supabaseUrl.isNullOrBlank(), TAG, "EDIFIKANA_SUPABASE_URL cannot be blank")
        supabaseUrl
    }

    single(named(EDIFIKANA_SUPABASE_KEY)) {
        val supabaseKey = System.getenv("EDIFIKANA_SUPABASE_KEY")
        assertFalse(supabaseKey.isNullOrBlank(), TAG, "EDIFIKANA_SUPABASE_KEY cannot be blank")
        supabaseKey
    }
}

private const val TAG = "ManagerPlatformModule"
