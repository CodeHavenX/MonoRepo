package com.cramsan.runasimi.service.di

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.core.BEDispatcherProvider
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.ktor.DiscordErrorCallbackDelegateService
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
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate
import com.cramsan.framework.preferences.implementation.JVMPreferencesDelegate
import com.cramsan.framework.preferences.implementation.PreferencesImpl
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import io.ktor.server.config.ApplicationConfig
import org.apache.logging.log4j.Logger
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the framework level components.
 */
val FrameworkModule = module(createdAtStart = true) {
    single<PreferencesDelegate> { JVMPreferencesDelegate() }

    single<Preferences> { PreferencesImpl(get()) }

    single<Logger> {
        Log4J2Helpers.getRootLogger(get(), Severity.INFO)
    }

    single<EventLoggerDelegate> { LoggerJVM(get()) }

    single {
        val config: ApplicationConfig = get()

        Severity.fromStringOrDefault(config.propertyOrNull("common.log_level")?.getString())
    }

    single<EventLoggerErrorCallbackDelegate> {
        DiscordErrorCallbackDelegateService(
            get(),
            get(named(DISCORD_ERROR_LOG_CHANNEL_ID_NAME)),
            get(),
        )
    }

    single<EventLoggerErrorCallback> {
        EventLoggerErrorCallbackImpl(
            get(),
            get(),
        )
    }

    single<EventLoggerInterface> {
        val instance = EventLoggerImpl(get(), get(), get())
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

    single(named(DISCORD_ERROR_LOG_CHANNEL_ID_NAME)) {
        val config: ApplicationConfig = get()

        config.propertyOrNull("kord.error_log_channel_id")?.getString() ?: ""
    }
}

const val DISCORD_ERROR_LOG_CHANNEL_ID_NAME = "DISCORD_ERROR_LOG_CHANNEL_ID_NAME"
