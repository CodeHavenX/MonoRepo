package com.cramsan.runasimi.client.lib.di

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
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.apache.logging.log4j.Logger
import org.koin.dsl.module

internal actual val FrameworkPlatformDelegatesModule = module {

    single<ThreadUtilDelegate> {
        ThreadUtilJVM(
            get(),
            get(),
        )
    }

    single {
        Severity.VERBOSE
    }

    single<Logger> {
        Log4J2Helpers.getRootLogger(true, get())
    }

    single<EventLoggerErrorCallbackDelegate> { NoopEventLoggerErrorCallbackDelegate() }

    single<EventLoggerDelegate> { LoggerJVM(get(), get()) }

    single<HaltUtilDelegate> {
        HaltUtilJVM(get())
    }

    single<DispatcherProvider> { UIDispatcherProvider() }

    single<PreferencesDelegate> { JVMPreferencesDelegate("runasimi-client") }
}
