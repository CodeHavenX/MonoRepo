package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.edifikana.server.dependencyinjection.settings.Overrides
import com.cramsan.framework.configuration.Configuration
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

val SettingsModule = module {
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

    factory<String>(named(Overrides.KEY_ALLOWED_HOST)) {
        val configuration = get<Configuration>()
        val configurationSetting = configuration.readString("edifikana${getStageSegment()}.allowed.host").orEmpty()
        configurationSetting.ifBlank {
            System.getenv("EDIFIKANA_ALLOWED_HOST").orEmpty()
        }
    }
}

internal fun Scope.getStageSegment(): String {
    val stage = get<String>(named(STAGE_KEY)).trim()
    return if (stage.isEmpty()) {
        ".local"
    } else {
        ".$stage"
    }
}
