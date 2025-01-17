package com.cramsan.edifikana.server.di

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.core.BEDispatcherProvider
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.Log4J2Helpers
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.apache.logging.log4j.Logger
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the framework level components.
 */
val FrameworkModule = module(createdAtStart = true) {
    single<PreferencesDelegate> { JVMPreferencesDelegate() }

    single<Preferences> { PreferencesImpl(get()) }

    single {
        Severity.fromStringOrDefault(System.getenv(NAME_LOGGING), Severity.DEBUG)
    }

    single(named(NAME_LOG_TO_FILE)) {
        System.getenv(NAME_LOG_TO_FILE).toBoolean()
    }

    single<Logger> {
        Log4J2Helpers.getRootLogger(get(named(NAME_LOG_TO_FILE)), get())
    }

    single<EventLoggerDelegate> { LoggerJVM(get()) }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(
            get(),
            NoopEventLoggerErrorCallbackDelegate,
        )
    }

    single<EventLoggerInterface> {
        val severity: Severity = get()
        val instance = EventLoggerImpl(severity, get(), get())
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }

    single<HaltUtilDelegate> { HaltUtilJVM() }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<AssertUtilInterface>(createdAtStart = true) {
        val impl = AssertUtilImpl(
            get(named(NAME_LOG_TO_FILE)),
            get(),
            get(),
        )
        AssertUtil.setInstance(impl)
        AssertUtil.singleton
    }

    single<ThreadUtilDelegate> { ThreadUtilJVM(get(), get()) }

    single<ThreadUtilInterface> {
        val instance = ThreadUtilImpl(get())
        ThreadUtil.setInstance(instance)
        ThreadUtil.singleton
    }

    single<DispatcherProvider> { BEDispatcherProvider() }
}

// Environment variables
const val NAME_LOGGING = "EDIFIKANA_LOGGING"
const val NAME_LOG_TO_FILE = "EDIFIKANA_LOG_TO_FILE"
