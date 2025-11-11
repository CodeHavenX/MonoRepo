package com.cramsan.architecture.server.dependencyinjection

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.utils.time.Chronos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Koin module for initializing core architecture-level dependencies.
 * This module provides essential components like coroutine scopes, clock implementations,
 * and settings holders that are used across the application.
 */
@OptIn(ExperimentalTime::class)
val ArchitectureModule = module(createdAtStart = true) {

    @OptIn(DelicateCoroutinesApi::class)
    single<CoroutineScope> {
        GlobalScope
    }

    single<Clock> {
        Chronos.initializeClock(clock = Clock.System)
        Chronos.clock()
    }

    single {
        SettingsHolder(get())
    }
}
