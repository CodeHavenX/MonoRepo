package com.cramsan.flyerboard.client.lib.di

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.flyerboard.client.lib.managers.AuthManager
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.client.lib.managers.UserManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module that registers all manager components.
 */
internal val ManagerModule = module {
    singleOf(::AuthManager)
    singleOf(::UserManager)
    singleOf(::FlyerManager)
    singleOf(::PreferencesManager)
}
