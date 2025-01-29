package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.dummy.DummyAuthService
import com.cramsan.edifikana.client.lib.service.dummy.DummyStorageService
import com.cramsan.edifikana.client.lib.service.impl.AuthServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StorageServiceImpl
import com.cramsan.framework.preferences.Preferences
import org.koin.dsl.module

val SupabaseOverridesModule = module {

    single<AuthService> {
        val preferences = get<Preferences>()

        if (preferences.loadBoolean(DEBUG_KEY) == true) {
            DummyAuthService()
        } else {
            get<AuthServiceImpl>()
        }
    }

    single<StorageService> {
        val preferences = get<Preferences>()

        if (preferences.loadBoolean(DEBUG_KEY) == true) {
            DummyStorageService()
        } else {
            get<StorageServiceImpl>()
        }
    }
}

const val DEBUG_KEY = "DEBUG_KEY"
