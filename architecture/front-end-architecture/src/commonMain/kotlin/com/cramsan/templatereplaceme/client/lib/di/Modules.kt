package com.cramsan.templatereplaceme.client.lib.di

import org.koin.core.module.Module

/**
 * This file contains the list of Koin modules used in the application. Most modules are loaded on an as-needed basis.
 * This means that the order in which they are loaded does not matter. Despite this we are keeping the order bases on
 * the estimated dependency order of the modules. This is to make it easier to understand the dependencies between the
 * modules.
 */

/**
 * Generate the list of Koin modules that are loaded at the application level.
 */
@Suppress("LongParameterList")
fun moduleList(
    frameworkModule: Module = FrameworkModule,
    frameworkPlatformDelegatesModule: Module = FrameworkPlatformDelegatesModule,
    extrasModule: Module = ExtrasModule,
    extrasPlatformModule: Module = ExtrasPlatformModule,
    ktorModule: Module = KtorModule,
    cacheModule: Module = CacheModule,
    applicationViewModelModule: Module,
    serviceModule: Module,
    servicePlatformModule: Module,
    managerModule: Module,
    managerPlatformModule: Module,
    viewModelModule: Module,
    viewModelPlatformModule: Module,
) = listOf(
    frameworkModule,
    frameworkPlatformDelegatesModule,
    extrasModule,
    extrasPlatformModule,
    ktorModule,
    cacheModule,
    applicationViewModelModule,
    serviceModule,
    servicePlatformModule,
    managerModule,
    managerPlatformModule,
    viewModelModule,
    viewModelPlatformModule,
)
