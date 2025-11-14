package com.cramsan.architecture.client.di

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.UIDispatcherProvider
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.Log4J2Helpers
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.apache.logging.log4j.Logger
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * JVM-specific implementation of the FrameworkPlatformDelegatesModule.
 * Provides JVM-specific implementations for threading, logging, halt utilities,
 * dispatcher providers, and preferences storage.
 */
internal actual val FrameworkPlatformDelegatesModule = module {

    single<ThreadUtilDelegate> {
        ThreadUtilJVM(
            get(),
            get(),
        )
    }

    single<Logger> {
        val settingsHolder: SettingsHolder = get()

        Log4J2Helpers.getRootLogger(
            settingsHolder.getBoolean(FrontEndApplicationSettingKey.LoggingEnableFileLogging) ?: false,
            get(),
        )
    }

    single<EventLoggerErrorCallbackDelegate> { NoopEventLoggerErrorCallbackDelegate() }

    single<EventLoggerDelegate> { LoggerJVM(get(), get()) }

    single<HaltUtilDelegate> {
        HaltUtilJVM(get())
    }

    single<DispatcherProvider> { UIDispatcherProvider() }

    single<PreferencesDelegate> { JVMPreferencesDelegate(get(named(NamedDependency.DOMAIN_KEY))) }
}
