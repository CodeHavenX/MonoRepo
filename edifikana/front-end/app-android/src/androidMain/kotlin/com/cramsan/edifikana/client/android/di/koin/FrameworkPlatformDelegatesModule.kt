package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.android.framework.crashhandler.CrashlyticsCrashHandler
import com.cramsan.edifikana.client.android.framework.crashhandler.CrashlyticsErrorCallback
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.DispatcherProviderImpl
import com.cramsan.framework.crashehandler.CrashHandlerDelegate
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilAndroid
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.implementation.LoggerAndroid
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.implementation.ThreadUtilAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val FrameworkPlatformDelegatesModule = module(createdAtStart = true) {

    single<ThreadUtilDelegate> {
        ThreadUtilAndroid(get())
    }

    single<CrashHandlerDelegate> { CrashlyticsCrashHandler() }

    single<EventLoggerErrorCallbackDelegate> {
        CrashlyticsErrorCallback()
    }

    single<EventLoggerDelegate> { LoggerAndroid() }

    single<HaltUtilDelegate> {
        HaltUtilAndroid(androidApplication())
    }

    single<DispatcherProvider> { DispatcherProviderImpl() }
}
