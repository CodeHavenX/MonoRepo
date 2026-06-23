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
        //       callback), add a scheme-alias resolver next to TemplateReplaceMePathNavigation
        //       (the KSP-generated path aggregator) instead of registering it here — see
        //       Edifikana's EdifikanaExternalUrlResolver.kt for the pattern.
    }
