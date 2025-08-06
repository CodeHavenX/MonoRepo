package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.core.controller.EventLogController
import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.PropertyController
import com.cramsan.edifikana.server.core.controller.StaffController
import com.cramsan.edifikana.server.core.controller.StorageController
import com.cramsan.edifikana.server.core.controller.TimeCardController
import com.cramsan.edifikana.server.core.controller.UserController
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.StorageService
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.settings.Overrides
import com.cramsan.framework.annotations.TestOnly
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.configuration.Configuration
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
import io.mockk.mockk
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
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
        Severity.DEBUG
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
}

/**
 * Produce a Ktor module for testing.
 */
fun testKtorModule() = module {
    singleOf(::UserController)
    singleOf(::EventLogController)
    singleOf(::HealthCheckController)
    singleOf(::PropertyController)
    singleOf(::StaffController)
    singleOf(::TimeCardController)
    singleOf(::StorageController)

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
    single<EventLogService> { mockk() }
    single<PropertyService> { mockk() }
    single<StaffService> { mockk() }
    single<TimeCardService> { mockk() }
    single<StorageService> { mockk() }
}

fun testSettingsModule() = module {
    factory<String>(named(Overrides.KEY_ALLOWED_HOST)) { "" }
}
