package com.cramsan.samples.service.ktor.di

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
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.Log4J2Helpers
import com.cramsan.framework.logging.implementation.LoggerJVM
import org.apache.logging.log4j.Logger
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the framework level components.
 */
val FrameworkModule = module(createdAtStart = true) {
    single(named(IS_DEBUG_NAME)) { false }

    single<Logger> {
        Log4J2Helpers.getRootLogger(get(named(IS_DEBUG_NAME)), Severity.INFO)
    }

    single<EventLoggerDelegate> { LoggerJVM(get()) }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(
            get(),
            object : EventLoggerErrorCallbackDelegate {
                override fun handleErrorEvent(
                    tag: String,
                    message: String,
                    throwable: Throwable,
                    severity: Severity,
                ) = Unit
            },
        )
    }

    single<EventLoggerInterface> {
        val severity: Severity = when (get<Boolean>(named(IS_DEBUG_NAME))) {
            true -> Severity.VERBOSE
            false -> Severity.VERBOSE
        }
        val instance = EventLoggerImpl(severity, get(), get())
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

    single<DispatcherProvider> { BEDispatcherProvider() }
}

private const val IS_DEBUG_NAME = "isDebugEnabled"
