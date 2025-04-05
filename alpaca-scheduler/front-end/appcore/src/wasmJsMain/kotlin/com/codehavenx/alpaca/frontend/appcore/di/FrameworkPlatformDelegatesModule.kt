package com.codehavenx.alpaca.frontend.appcore.di

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilDelegateImpl
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.InMemoryPreferencesDelegate
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implemantation.ThreadUtilDelegateNoop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val FrameworkPlatformDelegatesModule = module {

    single<ThreadUtilDelegate> {
        ThreadUtilDelegateNoop()
    }

    single<EventLoggerErrorCallbackDelegate> { NoopEventLoggerErrorCallbackDelegate() }

    single<EventLoggerDelegate> { StdOutEventLoggerDelegate() }

    single<HaltUtilDelegate> { HaltUtilDelegateImpl() }

    single<DispatcherProvider> {
        object : DispatcherProvider {
            override fun ioDispatcher(): CoroutineDispatcher { return Dispatchers.Default }

            override fun uiDispatcher(): CoroutineDispatcher { return Dispatchers.Default }
        }
    }

    single<PreferencesDelegate> { InMemoryPreferencesDelegate() }
}
