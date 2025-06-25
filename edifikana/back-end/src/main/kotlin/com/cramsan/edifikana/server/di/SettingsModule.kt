package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.settings.Overrides
import com.cramsan.framework.configuration.Configuration
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

val SettingsModule = module {
    factory<Boolean>(named(Overrides.KEY_SUPABASE_DISABLE)) {
        val configuration = get<Configuration>()
        configuration.readBoolean("edifikana.supabase.disable") ?: false
    }

    factory<String>(named(Overrides.KEY_SUPABASE_KEY)) {
        val configuration = get<Configuration>()
        val configSetting = configuration.readString("edifikana${getStageSegment()}.supabase.key").orEmpty()
        configSetting.ifBlank {
            System.getenv("EDIFIKANA_SUPABASE_KEY").orEmpty()
        }
    }

    factory<String>(named(Overrides.KEY_SUPABASE_URL)) {
        val configuration = get<Configuration>()
        val configSetting = configuration.readString("edifikana${getStageSegment()}.supabase.url").orEmpty()
        configSetting.ifBlank {
            System.getenv("EDIFIKANA_SUPABASE_URL").orEmpty()
        }
    }
}

private fun Scope.getStageSegment(): String {
    val stage = get<String>(named(STAGE_KEY)).trim()
    return if (stage.isEmpty()) {
        ".local"
    } else {
        ".$stage"
    }
}
