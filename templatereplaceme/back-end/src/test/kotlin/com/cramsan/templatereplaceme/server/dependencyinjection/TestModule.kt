package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.annotations.TestOnly
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.configuration.Configuration
import com.cramsan.framework.configuration.ConfigurationMultiplexer
import com.cramsan.framework.configuration.NoopConfiguration
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.Log4J2Helpers
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.logging.implementation.NoopEventLoggerDelegate
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import com.cramsan.framework.utils.time.Chronos
import com.cramsan.templatereplaceme.lib.serialization.createJson
import com.cramsan.templatereplaceme.server.SettingsHolder
import com.cramsan.templatereplaceme.server.controller.HealthCheckController
import com.cramsan.templatereplaceme.server.controller.UserController
import com.cramsan.templatereplaceme.server.controller.authentication.ContextRetriever
import com.cramsan.templatereplaceme.server.service.UserService
import io.mockk.mockk
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Produce a framework module for testing.
 */
fun testFrameworkModule() = module(createdAtStart = true) {
    single<Logger> {
        Log4J2Helpers.getRootLogger(false, get())
    }

    single {
        Severity.VERBOSE
    }

    single<EventLoggerInterface> {
        val instance = EventLoggerImpl(get(), null, LoggerJVM(get(), get()))
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }
    single<HaltUtilDelegate> { HaltUtilJVM(PassthroughEventLogger(NoopEventLoggerDelegate())) }
    single<HaltUtil> { HaltUtilImpl(get()) }
    single<AssertUtilInterface>(createdAtStart = true) {
        val impl = AssertUtilImpl(
            false,
            get(),
            get(),
        )
        AssertUtil.setInstance(impl)
        AssertUtil.singleton
    }
    single<ThreadUtilDelegate> { ThreadUtilJVM(get(), get()) }
    single<ThreadUtilInterface> {
        ThreadUtilImpl(get())
    }
    single<Configuration> {
        NoopConfiguration()
    }
    single {
        ConfigurationMultiplexer()
    }
}

/**
 * Produce a Ktor module for testing.
 */
fun testKtorModule() = module {
    singleOf(::UserController)
    singleOf(::HealthCheckController)

    registerControllers()
}

/**
 * Produce a test module for the application.
 */
@OptIn(TestOnly::class, ExperimentalTime::class)
fun testApplicationModule() = module {
    single<Json> { createJson() }

    single<Clock> {
        Chronos.initializeClock(clock = mockk())
        Chronos.setTimeZoneOverride(TimeZone.UTC)
        Chronos.clock()
    }

    single<ContextRetriever> { mockk() }

    // Services
    single<UserService> { mockk() }

    single { SettingsHolder(get()) }
}
