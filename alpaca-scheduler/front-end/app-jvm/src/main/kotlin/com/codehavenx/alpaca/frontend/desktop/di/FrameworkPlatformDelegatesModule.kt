package com.codehavenx.alpaca.frontend.desktop.di

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.UIDispatcherProvider
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.Log4J2Helpers
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.apache.logging.log4j.Logger
import org.koin.dsl.module

val FrameworkPlatformDelegatesModule = module(createdAtStart = true) {

    single<ThreadUtilDelegate> {
        ThreadUtilJVM(
            get(),
            get(),
        )
    }

    single<Logger> {
        Log4J2Helpers.getRootLogger(true, Severity.VERBOSE)
    }

    single<EventLoggerErrorCallbackDelegate> { NoopEventLoggerErrorCallbackDelegate }

    single<EventLoggerDelegate> { LoggerJVM(get()) }

    single<HaltUtilDelegate> {
        HaltUtilJVM()
    }

    single<DispatcherProvider> { UIDispatcherProvider() }

    single<PreferencesDelegate> { JVMPreferencesDelegate() }

    single<Preferences> {
        PreferencesImpl(get())
    }
}
