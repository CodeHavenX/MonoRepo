package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.TimeCardService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class TimeCardControllerTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createTimeCardEvent`() = testBackEndApplication {
        // Configure
        val requestBody = readFileContent("requests/create_timecard_event_request.json")
        val expectedResponse = readFileContent("requests/create_timecard_event_response.json")
        val timeCardService = get<TimeCardService>()
        val rbacService = get<RBACService>()
        val clock = get<Clock>()

        coEvery {
            timeCardService.createTimeCardEvent(
                employeeId = EmployeeId("emp123"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property123"),
                type = TimeCardEventType.CLOCK_OUT,
                imageUrl = "http://example.com/image.jpg",
                timestamp = Instant.fromEpochSeconds(1727702654),
            )
        }.answers {
            TimeCardEvent(
                id = TimeCardEventId("timecard123"),
                employeeId = EmployeeId("emp123"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property123"),
                type = TimeCardEventType.CLOCK_OUT,
                imageUrl = "http://example.com/image.jpg",
                timestamp = Instant.fromEpochSeconds(1727702654),
            )
        }
        every { clock.now() } returns Instant.fromEpochSeconds(1727702654)
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.post("time_card") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getTimeCardEvent`() = testBackEndApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_timecard_event_response.json")
        val timeCardService = get<TimeCardService>()
        val rbacService = get<RBACService>()
        coEvery {
            timeCardService.getTimeCardEvent(TimeCardEventId("timecard123"))
        }.answers {
            TimeCardEvent(
                id = TimeCardEventId("timecard123"),
                employeeId = EmployeeId("emp123"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property123"),
                type = TimeCardEventType.CLOCK_IN,
                imageUrl = "http://example.com/image.jpg",
                timestamp = Instant.fromEpochSeconds(1727702654),
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, TimeCardEventId("timecard123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.get("time_card/timecard123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getTimeCardEvents`() = testBackEndApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_timecard_events_response.json")
        val timeCardService = get<TimeCardService>()
        val rbacService = get<RBACService>()
        coEvery {
            timeCardService.getTimeCardEvents(EmployeeId("emp123"))
        }.answers {
            listOf(
                TimeCardEvent(
                    id = TimeCardEventId("timecard123"),
                    employeeId = EmployeeId("emp123"),
                    fallbackEmployeeName = "John Doe",
                    propertyId = PropertyId("property123"),
                    type = TimeCardEventType.CLOCK_IN,
                    imageUrl = "http://example.com/image.jpg",
                    timestamp = Instant.fromEpochSeconds(1727702654),
                ),
                TimeCardEvent(
                    id = TimeCardEventId("timecard456"),
                    employeeId = EmployeeId("emp456"),
                    fallbackEmployeeName = "Jane Smith",
                    propertyId = PropertyId("property123"),
                    type = TimeCardEventType.CLOCK_IN,
                    imageUrl = "http://example.com/image2.jpg",
                    timestamp = Instant.fromEpochSeconds(1727702654),
                )
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("emp123"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, EmployeeId("emp123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.get("time_card?propertyId=property123&employeeId=emp123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
