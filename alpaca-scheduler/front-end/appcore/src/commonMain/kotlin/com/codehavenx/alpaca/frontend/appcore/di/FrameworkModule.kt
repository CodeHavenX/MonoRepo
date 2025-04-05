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
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.dsl.module

val FrameworkModule = module(createdAtStart = true) {

    single<AssertUtilInterface> {
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
        EventLoggerErrorCallbackImpl(get(), get())
    }

    single {
        Severity.VERBOSE
    }

    single<EventLoggerInterface> {
        val instance =
            EventLoggerImpl(
                get(),
                get(),
                get(),
            )
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }
}
