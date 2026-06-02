package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.architecture.client.deeplink.DeepLinkManager
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.templatereplaceme.client.lib.features.main.MainDestination
import com.cramsan.templatereplaceme.client.lib.managers.UserManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ManagerModule =
    module {
        singleOf(::UserManager)
        singleOf(::PreferencesManager)

        single(createdAtStart = true) {
            get<DeepLinkManager>().register { params ->
                if (params.params["type"] == "demo") MainDestination.MenuDestination else null
            }
        }
    }
