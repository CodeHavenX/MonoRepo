package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.templatereplaceme.client.lib.managers.ComponentReplaceMeManager
import com.cramsan.templatereplaceme.client.lib.managers.InitializerManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Registers all front-end manager singletons with Koin.
 *
 * Add new managers here using [singleOf].
 */
internal val ManagerModule =
    module {
        singleOf(::InitializerManager)
        singleOf(::ComponentReplaceMeManager)
        singleOf(::PreferencesManager)

        // TODO: If this app needs a native custom-scheme deep link (e.g. an auth-provider
        //       callback), make sure the redirect/callback URL embeds the destination's
        //       @WebPath as its path (e.g. yourscheme://host/your-canonical-path) — WebRoute
        //       strips the scheme/authority generically, so TemplateReplaceMePathNavigation
        //       (the KSP-generated path aggregator) resolves it with no extra wiring here.
    }
