package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.settings.Overrides
import com.cramsan.framework.configuration.Configuration
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val SettingsModule = module {
    factory<Boolean>(named(Overrides.KEY_SUPABASE_DISABLE)) {
        val configuration = get<Configuration>()
        configuration.readBoolean("edifikana.supabase.disable") ?: false
    }
}
