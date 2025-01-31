package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.UIDispatcherProvider
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilDelegateImpl
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.EventLoggerDelegateNoop
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.InMemoryPreferencesDelegate
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implemantation.ThreadUtilDelegateNoop
import org.koin.dsl.module

actual val FrameworkPlatformDelegatesModule = module {

    single<ThreadUtilDelegate> {
        ThreadUtilDelegateNoop
    }

    single<EventLoggerErrorCallbackDelegate> {
        NoopEventLoggerErrorCallbackDelegate
    }

    single<EventLoggerDelegate> {
        EventLoggerDelegateNoop
    }

    single<HaltUtilDelegate> {
        HaltUtilDelegateImpl
    }

    single<DispatcherProvider> {
        UIDispatcherProvider()
    }

    single<PreferencesDelegate> { InMemoryPreferencesDelegate() }
}
