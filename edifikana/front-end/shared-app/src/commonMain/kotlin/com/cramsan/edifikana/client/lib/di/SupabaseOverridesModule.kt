package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.dummy.DummyAuthService
import com.cramsan.edifikana.client.lib.service.dummy.DummyStorageService
import com.cramsan.edifikana.client.lib.service.impl.AuthServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StorageServiceImpl
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.client.lib.ui.di.Coil3Provider
import com.cramsan.edifikana.client.lib.ui.di.DummyCoil3Integration
import io.github.jan.supabase.coil.Coil3Integration
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val SupabaseOverridesModule = module {

    single<AuthService> {
        val disableSupabase = get<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE))
        if (disableSupabase) {
            DummyAuthService()
        } else {
            get<AuthServiceImpl>()
        }
    }

    single<StorageService> {
        val disableSupabase = get<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE))
        if (disableSupabase) {
            DummyStorageService()
        } else {
            get<StorageServiceImpl>()
        }
    }

    single<Coil3Provider> {
        val disableSupabase = get<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE))
        val coil3Integration = if (disableSupabase) {
            DummyCoil3Integration()
        } else {
            get<Coil3Integration>()
        }

        Coil3Provider(coil3Integration)
    }
}
