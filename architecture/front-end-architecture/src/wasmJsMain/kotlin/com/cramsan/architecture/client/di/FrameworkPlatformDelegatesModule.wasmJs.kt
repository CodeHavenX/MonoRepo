package com.cramsan.architecture.client.di

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.UIDispatcherProvider
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilDelegateImpl
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.BrowserLocalStoragePreferencesDelegate
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implemantation.ThreadUtilDelegateNoop
import org.koin.dsl.module

/**
 * WasmJS-specific implementation of the FrameworkPlatformDelegatesModule.
 * Provides web browser-specific implementations for threading, logging, halt utilities,
 * dispatcher providers, and preferences storage using browser APIs (localStorage).
 */
internal actual val FrameworkPlatformDelegatesModule = module {

    single<ThreadUtilDelegate> {
        ThreadUtilDelegateNoop()
    }

    single<EventLoggerErrorCallbackDelegate> {
        NoopEventLoggerErrorCallbackDelegate()
    }

    single<EventLoggerDelegate> {
        StdOutEventLoggerDelegate()
    }

    single<HaltUtilDelegate> {
        HaltUtilDelegateImpl()
    }

    single<DispatcherProvider> {
        UIDispatcherProvider()
    }

    single<PreferencesDelegate> { BrowserLocalStoragePreferencesDelegate() }
}
