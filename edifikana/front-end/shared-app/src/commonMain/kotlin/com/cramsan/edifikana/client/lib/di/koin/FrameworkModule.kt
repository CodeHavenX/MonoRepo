package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.implementation.EventLoggerErrorCallbackImpl
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val FrameworkModule = module {

    single<AssertUtilInterface> {
        AssertUtilImpl(
            get(named(Overrides.KEY_HALT_ON_FAILURE)),
            get(),
            get(),
        )
    }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(get(), get())
    }

    single<EventLoggerInterface> {
        EventLoggerImpl(
            get(named(Overrides.KEY_LOGGING_SEVERITY)),
            get(),
            get(),
        )
    }

    single<HaltUtil> { HaltUtilImpl(get()) }

    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }

    single<Preferences> {
        PreferencesImpl(get())
    }
}
