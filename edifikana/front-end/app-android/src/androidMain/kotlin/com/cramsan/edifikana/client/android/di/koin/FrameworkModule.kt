package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.android.BuildConfig
import com.cramsan.edifikana.client.android.framework.crashhandler.CrashlyticsCrashHandler
import com.cramsan.edifikana.client.android.framework.crashhandler.CrashlyticsErrorCallback
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.DispatcherProviderImpl
import com.cramsan.framework.crashehandler.CrashHandler
import com.cramsan.framework.crashehandler.CrashHandlerDelegate
import com.cramsan.framework.crashehandler.implementation.CrashHandlerImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilAndroid
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.LoggerAndroid
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilAndroid
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val FrameworkModule = module(createdAtStart = true) {

    single<AssertUtilInterface> {
        val impl = AssertUtilImpl(
            BuildConfig.DEBUG,
            get(),
            get(),
        )
        AssertUtil.setInstance(impl)
        AssertUtil.singleton
    }

    single<ThreadUtilDelegate> {
        ThreadUtilAndroid(get())
    }

    single<CrashHandlerDelegate> { CrashlyticsCrashHandler() }

    single<CrashHandler> {
        CrashHandlerImpl(get())
    }

    single<EventLoggerErrorCallbackDelegate> {
        CrashlyticsErrorCallback()
    }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(get(), get())
    }

    single<EventLoggerDelegate> { LoggerAndroid() }

    single<EventLoggerInterface> {
        val severity = if (BuildConfig.DEBUG) {
            Severity.VERBOSE
        } else {
            Severity.INFO
        }
        val instance =
            EventLoggerImpl(
                severity,
                get(),
                get(),
            )
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }

    single<HaltUtilDelegate> {
        HaltUtilAndroid(androidApplication())
    }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<ThreadUtilInterface> {
        val instance = ThreadUtilImpl(get())
        ThreadUtil.setInstance(instance)
        ThreadUtil.singleton
    }

    single<DispatcherProvider> { DispatcherProviderImpl() }
}
