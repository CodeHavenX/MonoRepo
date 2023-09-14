package com.codehavenx.platform.bot.di

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
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the framework level components.
 */
val FrameworkModule = module {
    single<PreferencesDelegate> { JVMPreferencesDelegate() }

    single<Preferences> { PreferencesImpl(get()) }

    single(named(IS_DEBUG_NAME)) { false }

    single<EventLoggerDelegate> { LoggerJVM(get(named(IS_DEBUG_NAME))) }

    single<EventLoggerInterface> {
        val severity: Severity = when (get<Boolean>(named(IS_DEBUG_NAME))) {
            true -> Severity.VERBOSE
            false -> Severity.VERBOSE
        }
        val instance = EventLoggerImpl(severity, null, get())
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }

    single<HaltUtilDelegate> { HaltUtilJVM() }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<AssertUtilInterface> {
        val impl = AssertUtilImpl(
            true,
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

private const val IS_DEBUG_NAME = "isDebugEnabled"
