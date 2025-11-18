package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.templatereplaceme.client.lib.managers.UserManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ManagerModule = module {
    singleOf(::UserManager)
    singleOf(::PreferencesManager)
}
