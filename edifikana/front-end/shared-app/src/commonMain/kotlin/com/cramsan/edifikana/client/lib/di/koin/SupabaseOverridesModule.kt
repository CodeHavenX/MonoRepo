package com.cramsan.edifikana.client.lib.di.koin

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

val SupabaseOverridesModule = module {

    single<AuthService> {
        val isDummyMode = get<Boolean>(named(Overrides.KEY_DUMMY_MODE))
        if (isDummyMode) {
            DummyAuthService()
        } else {
            get<AuthServiceImpl>()
        }
    }

    single<StorageService> {
        val isDummyMode = get<Boolean>(named(Overrides.KEY_DUMMY_MODE))
        if (isDummyMode) {
            DummyStorageService()
        } else {
            get<StorageServiceImpl>()
        }
    }

    single<Coil3Provider> {
        val isDummyMode = get<Boolean>(named(Overrides.KEY_DUMMY_MODE))
        val coil3Integration = if (isDummyMode) {
            DummyCoil3Integration()
        } else {
            get<Coil3Integration>()
        }

        Coil3Provider(coil3Integration)
    }
}
