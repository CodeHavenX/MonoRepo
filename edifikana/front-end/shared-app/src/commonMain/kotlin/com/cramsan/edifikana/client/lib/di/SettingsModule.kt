package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.preferences.Preferences
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val SettingsModule = module {
    factory<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE)) {
        val preferences = get<Preferences>()
        preferences.loadBoolean(Overrides.KEY_DISABLE_SUPABASE) ?: false
    }

    factory<Boolean>(named(Overrides.KEY_HALT_ON_FAILURE)) {
        val preferences = get<Preferences>()
        preferences.loadBoolean(Overrides.KEY_HALT_ON_FAILURE) ?: false
    }

    factory<Severity>(named(Overrides.KEY_LOGGING_SEVERITY)) {
        val preferences = get<Preferences>()
        Severity.fromStringOrDefault(preferences.loadString(Overrides.KEY_LOGGING_SEVERITY))
    }

    factory<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_URL)) {
        val preferences = get<Preferences>()
        preferences.loadString(Overrides.KEY_SUPABASE_OVERRIDE_URL) ?: ""
    }

    factory<String>(named(Overrides.KEY_SUPABASE_OVERRIDE_KEY)) {
        val preferences = get<Preferences>()
        preferences.loadString(Overrides.KEY_SUPABASE_OVERRIDE_KEY) ?: ""
    }

    factory<Boolean>(named(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED)) {
        val preferences = get<Preferences>()
        preferences.loadBoolean(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED) ?: false
    }

    factory<Boolean>(named(Overrides.KEY_DISABLE_BE)) {
        val preferences = get<Preferences>()
        preferences.loadBoolean(Overrides.KEY_DISABLE_BE) ?: false
    }
}
