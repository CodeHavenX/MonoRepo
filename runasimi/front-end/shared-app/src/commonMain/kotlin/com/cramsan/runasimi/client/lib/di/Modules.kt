package com.cramsan.runasimi.client.lib.di

/**
 * This file contains the list of Koin modules used in the application. Most modules are loaded on an as-needed basis.
 * This means that the order in which they are loaded does not matter. Despite this we are keeping the order bases on
 * the estimated dependency order of the modules. This is to make it easier to understand the dependencies between the
 * modules.
 */

/**
 * List of Koin modules that are loaded at the application level.
 */
val moduleList = listOf(
    FrameworkModule,
    FrameworkPlatformDelegatesModule,
    ExtrasModule,
    ExtrasPlatformModule,
    ManagerModule,
    ApplicationViewModelModule,
    ViewModelModule,
    ViewModelPlatformModule,
)
