package com.cramsan.runasimi.service

import com.cramsan.runasimi.service.di.createApplicationModule
import com.cramsan.runasimi.service.di.createFrameworkModule
import com.cramsan.framework.core.ktor.service.DiscordService
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.logW
import com.cramsan.framework.test.TestBase
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.Test

class ApplicationTest : TestBase() {

    @MockK(relaxed = true)
    private lateinit var discordService: DiscordService

    override fun setupTest() { }

    @Test
    fun `verify app launching`() = runBlockingTest {
        MockKAnnotations.init(this) // turn relaxUnitFun on for all mocks

        val eventLogger: EventLoggerInterface = mockk(relaxed = true)

        testApplication {
            startKoin {
                modules(
                    createFrameworkModule(
                        eventLoggerInterface = eventLogger,
                    ),
                    createApplicationModule(
                        discordService = discordService,
                        scope = backgroundScope,
                    ),
                )
            }
        }

        verify {
            eventLogger.log(Severity.VERBOSE, "EventLoggerImpl", "Probing logger for severity: VERBOSE", any(), any())
            eventLogger.log(Severity.DEBUG, "EventLoggerImpl", "Probing logger for severity: DEBUG", any(), any())
            eventLogger.log(Severity.INFO, "EventLoggerImpl", "Probing logger for severity: INFO", any(), any())
            eventLogger.log(Severity.WARNING, "EventLoggerImpl", "Probing logger for severity: WARNING", any(), any())
            eventLogger.log(Severity.ERROR, "EventLoggerImpl", "Probing logger for severity: ERROR", any(), any())
            eventLogger.log(Severity.DISABLED, "EventLoggerImpl", "Probing logger for severity: DISABLED", any(), any())
            eventLogger.targetSeverity
        }

        verify {
            eventLogger.i("Application", "Application is ready.")
            eventLogger.i("Application", "ApplicationStarted")
            eventLogger.i("Application", "ApplicationStopPreparing")
            eventLogger.i("Application", "ApplicationStopping")
            eventLogger.i("Application", "ApplicationStopped")
        }
        confirmVerified(eventLogger)

        stopKoin()
    }
}