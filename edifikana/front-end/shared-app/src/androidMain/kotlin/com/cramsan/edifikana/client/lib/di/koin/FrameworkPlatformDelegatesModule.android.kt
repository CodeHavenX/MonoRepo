package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.DispatcherProviderImpl
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilAndroid
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.LoggerAndroid
import com.cramsan.framework.logging.implementation.NoopEventLoggerErrorCallbackDelegate
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.PreferencesAndroid
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implementation.ThreadUtilAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val FrameworkPlatformDelegatesModule = module {

    single<ThreadUtilDelegate> {
        ThreadUtilAndroid(get())
    }

    single<EventLoggerErrorCallbackDelegate> { NoopEventLoggerErrorCallbackDelegate() }

    single<EventLoggerDelegate> { LoggerAndroid() }

    single<HaltUtilDelegate> {
        HaltUtilAndroid(androidApplication())
    }

    single<DispatcherProvider> { DispatcherProviderImpl() }

    single<PreferencesDelegate> { PreferencesAndroid(androidApplication()) }
}
