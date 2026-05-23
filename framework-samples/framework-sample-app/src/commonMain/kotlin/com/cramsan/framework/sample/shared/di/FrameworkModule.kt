package com.cramsan.framework.sample.shared.di

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.configuration.Configuration
import com.cramsan.framework.configuration.NoopConfiguration
import com.cramsan.framework.crashhandler.CrashHandler
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.metrics.MetricsInterface
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.remoteconfig.RemoteConfig
import com.cramsan.framework.sample.shared.stubs.NoopCrashHandlerDelegate
import com.cramsan.framework.sample.shared.stubs.NoopMetrics
import com.cramsan.framework.sample.shared.stubs.SampleRemoteConfig
import com.cramsan.framework.sample.shared.stubs.SampleRemoteConfigPayload
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.userevents.UserEventsInterface
import com.cramsan.framework.userevents.implementation.NoopUserEvents
import org.koin.dsl.module

internal val FrameworkModule =
    module {

        single<AssertUtilInterface> {
            AssertUtilImpl(
                false,
                get(),
                get(),
            ).also {
                AssertUtil.setInstance(it)
            }
        }

        single<EventLoggerErrorCallback> {
            EventLoggerErrorCallbackImpl(get(), get())
        }

        single<EventLoggerInterface> {
            EventLoggerImpl(
                targetSeverity = Severity.VERBOSE,
                get(),
                get(),
            ).also {
                EventLogger.setInstance(it)
            }
        }

        single<HaltUtil> { HaltUtilImpl(get()) }

        single<ThreadUtilInterface> { get<ThreadUtilDelegate>() }

        single<Preferences> { get<PreferencesDelegate>() }

        single<Configuration> { NoopConfiguration() }

        single<CrashHandler> { NoopCrashHandlerDelegate() }

        single<MetricsInterface> { NoopMetrics() }

        single<UserEventsInterface> { NoopUserEvents() }

        single<RemoteConfig<SampleRemoteConfigPayload>> { SampleRemoteConfig() }
    }
