package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.dsl.module

val FrameworkModule = module {

    single<AssertUtilInterface> {
        AssertUtilImpl(
            true,
            get(),
            get(),
        )
    }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(get(), get())
    }

    single<EventLoggerInterface> {
        val severity = if (true) {
            Severity.VERBOSE
        } else {
            Severity.INFO
        }
        EventLoggerImpl(
            severity,
            get(),
            get(),
        )
    }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }
}
