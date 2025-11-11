package com.cramsan.architecture.server.test.dependencyinjection

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.annotations.TestOnly
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.configuration.Configuration
import com.cramsan.framework.configuration.ConfigurationMultiplexer
import com.cramsan.framework.configuration.NoopConfiguration
import com.cramsan.framework.configuration.SimpleConfiguration
import com.cramsan.framework.core.ktor.Controller
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
import com.cramsan.framework.test.asClock
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import com.cramsan.framework.utils.time.Chronos
import kotlinx.datetime.TimeZone
import kotlinx.datetime.asClock
import org.apache.logging.log4j.Logger
import org.koin.dsl.module
import kotlin.collections.List
import kotlin.time.Clock
import kotlin.time.TestTimeSource

/**
 * Produce a framework module for testing.
 */
val TestFrameworkModule = module(createdAtStart = true) {
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
    single<AssertUtilInterface> {
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
 * Produce a framework module for integration tests.
 */
val IntegTestFrameworkModule = module(createdAtStart = true) {
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
    single<AssertUtilInterface> {
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

    single<SimpleConfiguration> {
        SimpleConfiguration("config.properties.integ")
    }

    single {
        val configurationMultiplexer = ConfigurationMultiplexer()
        val simpleConfiguration: SimpleConfiguration = get()
        configurationMultiplexer.setConfigurations(
            listOf(
                simpleConfiguration, // For integ tests, only use the simple configuration
            )
        )
        configurationMultiplexer
    }
}

val TestKtorModule = module {

    single<List<Controller>> {
        getAll<Controller>()
    }
}

@OptIn(TestOnly::class)
val TestArchitectureModule = module(createdAtStart = true) {

    single {
        TestTimeSource()
    }

    single<Clock> {
        val testTimeSource: TestTimeSource = get()
        val clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        Chronos.forceInitializeClock(clock)
        Chronos.setTimeZoneOverride(TimeZone.UTC)
        Chronos.clock()
    }

    single { SettingsHolder(get()) }
}
