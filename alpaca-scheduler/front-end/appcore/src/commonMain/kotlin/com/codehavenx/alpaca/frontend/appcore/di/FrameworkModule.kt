package com.codehavenx.alpaca.frontend.appcore.di

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
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.dsl.module

val FrameworkModule = module(createdAtStart = true) {

    single<AssertUtilInterface> {
        println("AssertUtilInterface")
        // TODO: Move this flag into an injectable property
        val impl = AssertUtilImpl(
            true,
            get(),
            get(),
        )
        AssertUtil.setInstance(impl)
        AssertUtil.singleton
    }

    single<EventLoggerErrorCallback> {
        println("EventLoggerErrorCallback")
        EventLoggerErrorCallbackImpl(get(), get())
    }

    single<EventLoggerInterface> {
        println("EventLoggerInterface")
        // TODO: Move this flag into an injectable property
        val severity = if (true) {
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

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<ThreadUtilInterface> {
        val instance = ThreadUtilImpl(get())
        ThreadUtil.setInstance(instance)
        ThreadUtil.singleton
    }
}
