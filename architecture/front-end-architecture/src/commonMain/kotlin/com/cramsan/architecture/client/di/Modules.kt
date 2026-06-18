package com.cramsan.architecture.client.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * This file contains the list of Koin modules used in the application. Most modules are loaded on an as-needed basis.
 * This means that the order in which they are loaded does not matter. Despite this we are keeping the order bases on
 * the estimated dependency order of the modules. This is to make it easier to understand the dependencies between the
 * modules.
 *
 * Generate the list of Koin modules that are loaded at the application level.
 *
 * @param platformIsDebugBuild Whether the running binary is a debug build. AGP's Kotlin Multiplatform
 * library plugin has no build-type variants, so this can't be determined from within this library —
 * platforms that still have real variants (e.g. an Android application module) should pass their own
 * generated BuildConfig.DEBUG here. Platforms without a debug/release distinction should leave the default.
 */
@Suppress("LongParameterList")
fun moduleList(
    frameworkModule: Module = FrameworkModule,
    frameworkPlatformDelegatesModule: Module = FrameworkPlatformDelegatesModule,
    extrasModule: Module = ExtrasModule,
    extrasPlatformModule: Module = ExtrasPlatformModule,
    ktorModule: Module = KtorModule,
    databaseModule: Module,
    cacheModule: Module,
    applicationModule: Module,
    serviceModule: Module,
    servicePlatformModule: Module,
    managerModule: Module,
    managerPlatformModule: Module,
    viewModelModule: Module,
    viewModelPlatformModule: Module,
    platformIsDebugBuild: Boolean = false,
) = listOf(
    frameworkModule,
    frameworkPlatformDelegatesModule,
    extrasModule,
    extrasPlatformModule,
    ktorModule,
    databaseModule,
    cacheModule,
    applicationModule,
    serviceModule,
    servicePlatformModule,
    managerModule,
    managerPlatformModule,
    viewModelModule,
    viewModelPlatformModule,
    module {
        single(named(ApplicationIdentifier.PLATFORM_IS_DEBUG_BUILD)) { platformIsDebugBuild }
    },
)
