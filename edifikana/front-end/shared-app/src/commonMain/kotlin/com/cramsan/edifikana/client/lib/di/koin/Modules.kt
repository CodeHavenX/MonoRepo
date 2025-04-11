package com.cramsan.edifikana.client.lib.di.koin

/**
 * This file contains the list of Koin modules used in the application. Most modules are loaded on an as-needed basis.
 * This means that the order in which they are loaded does not matter. Despite this we are keeping the order bases on
 * the estimated dependency order of the modules. This is to make it easier to understand the dependencies between the
 * modules.
 */
val moduleList = listOf(
    FrameworkModule,
    FrameworkPlatformDelegatesModule,
    SettingsModule,
    ExtrasModule,
    ExtrasPlatformModule,
    KtorModule,
    ManagerModule,
    ManagerOverridesModule,
    ManagerPlatformModule,
    CacheModule,
    SupabaseModule,
    SupabaseOverridesModule,
    ViewModelModule,
    ViewModelPlatformModule,
)
