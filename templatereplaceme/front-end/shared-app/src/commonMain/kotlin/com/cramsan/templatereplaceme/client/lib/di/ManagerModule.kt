package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.architecture.client.deeplink.DeepLinkManager
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.templatereplaceme.client.lib.features.main.MainDestination
import com.cramsan.templatereplaceme.client.lib.managers.UserManager
import com.cramsan.templatereplaceme.client.lib.managers.PingPongManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ManagerModule =
    module {
        singleOf(::PingPongManager)
        singleOf(::PreferencesManager)

        // Example deep link handler for WASM testing: open http://localhost:8080/#type=demo
        // to verify the full routing pipeline (hash capture → DeepLinkManager → navigate).
        // Replace with your real handlers when building production features.
        single(createdAtStart = true) {
            get<DeepLinkManager>().register { params ->
                if (params.params["type"] == "demo") MainDestination.MenuDestination else null
            }
        }
    }
