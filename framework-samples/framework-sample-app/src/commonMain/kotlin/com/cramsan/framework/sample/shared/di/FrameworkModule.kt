package com.cramsan.framework.sample.shared.di

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.dsl.module

internal val FrameworkModule = module {

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

    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }

    single<Preferences> {
        PreferencesImpl(get())
    }
}
