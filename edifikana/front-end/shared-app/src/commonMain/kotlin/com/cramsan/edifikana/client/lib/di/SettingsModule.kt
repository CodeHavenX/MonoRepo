package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.mappers.PreferencesMapper
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.preferences.Preferences
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val SettingsModule = module {
    factory<Boolean>(named(Overrides.KEY_HALT_ON_FAILURE)) {
        val preferences = get<Preferences>()
        PreferencesMapper.haltOnFailure(preferences)
    }

    factory<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_URL)) {
        val preferences = get<Preferences>()
        PreferencesMapper.getSupabaseOverrideUrl(preferences)
    }

    factory<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_KEY)) {
        val preferences = get<Preferences>()
        PreferencesMapper.getSupabaseOverrideKey(preferences)
    }

    factory<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED)) {
        val preferences = get<Preferences>()
        PreferencesMapper.isSupabaseOverrideEnabled(preferences)
    }

    factory<String>(named(Overrides.KEY_EDIFIKANA_BE_URL)) {
        val preferences = get<Preferences>()
        PreferencesMapper.getEdifikanaBackendUrl(preferences)
    }

    factory<Boolean>(named(Overrides.KEY_EDIFIKANA_BE_OVERRIDE_ENABLED)) {
        val preferences = get<Preferences>()
        PreferencesMapper.isEdifikanaBackendOverrideEnabled(preferences)
    }

    factory<Boolean>(named(Overrides.KEY_OPEN_DEBUG_WINDOW)) {
        val preferences = get<Preferences>()
        PreferencesMapper.isOpenDebugWindow(preferences)
    }

    factory<String>(named(Overrides.KEY_LOGGING_SEVERITY_OVERRIDE)) {
        val preferences = get<Preferences>()
        PreferencesMapper.getLoggingSeverityOverride(preferences)
    }
}

const val DEFAULT_SUPABASE_URL = "http://127.0.0.1:54321"
const val DEFAULT_BE_URL = "http://127.0.0.1:9292"
