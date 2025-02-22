package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.settings.Overrides
import com.cramsan.edifikana.server.util.readSimpleProperty
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val SettingsModule = module {
    factory<Boolean>(named(Overrides.KEY_SUPABASE_DISABLE)) {
        readSimpleProperty("edifikana.supabase.disable").toBoolean()
    }
}
