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

        // TODO: Register deep link handlers here using DeepLinkManager once you have real
        //       navigation destinations. Example:
        //   single(createdAtStart = true) {
        //       get<DeepLinkManager>().register { params ->
        //           if (params.params["type"] == "demo") MyActivity.MyDestination else null
        //       }
        //   }
    }
