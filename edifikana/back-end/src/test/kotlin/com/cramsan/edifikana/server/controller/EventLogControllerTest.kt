package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.service.EventLogService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.EventLogEntry
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class EventLogControllerTest : CoroutineTest(), KoinTest {

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            createJson(),
            TestControllerModule,
            TestServiceModule,
        )
    }

    /**
     * Clean up the test.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // Something about public/private and the file name under the function was located.
    @Test
    fun `test createEventLog`() = testBackEndApplication {
        // Configure
        val requestBody = readFileContent("requests/create_event_log_entry_request.json")
        val expectedResponse = readFileContent("requests/create_event_log_entry_response.json")
        val rbacService = get<RBACService>()
        val userService = get<EventLogService>()
        coEvery {
            userService.createEventLogEntry(
                employeeId = EmployeeId("emp456"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.MAINTENANCE_SERVICE,
                fallbackEventType = "General Maintenance",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Routine Check",
                description = "Performed routine maintenance check.",
                unit = "Unit 101",
            )
        }.answers {
            EventLogEntry(
                id = EventLogEntryId("event123"),
                employeeId = EmployeeId("emp456"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.MAINTENANCE_SERVICE,
                fallbackEventType = "General Maintenance",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Routine Check",
                description = "Performed routine maintenance check.",
                unit = "Unit 101",
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
            rbacService.hasRoleOrHigher(context, PropertyId("property789"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.post("event_log") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEventLogEntry`() = testBackEndApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_event_log_entry_response.json")
        val userService = get<EventLogService>()
        val rbacService = get<RBACService>()
        coEvery {
            userService.getEventLogEntry(
                EventLogEntryId("event123"),
            )
        }.answers {
            EventLogEntry(
                id = EventLogEntryId("event123"),
                employeeId = EmployeeId("emp456"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.MAINTENANCE_SERVICE,
                fallbackEventType = "General Maintenance",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Routine Check",
                description = "Performed routine maintenance check.",
                unit = "Unit 101",
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
            rbacService.hasRoleOrHigher(context, EventLogEntryId("event123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.get("event_log/event123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Ignore
    @Test
    fun `test getEventLogEntries`() = testBackEndApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_event_log_entries_response.json")
        val userService = get<EventLogService>()
        val rbacService = get<RBACService>()
        coEvery {
            userService.getEventLogEntries()
        }.answers {
            listOf(
                EventLogEntry(
                    id = EventLogEntryId("event123"),
                    employeeId = EmployeeId("emp456"),
                    fallbackEmployeeName = "John Doe",
                    propertyId = PropertyId("property789"),
                    type = EventLogEventType.MAINTENANCE_SERVICE,
                    fallbackEventType = "General Maintenance",
                    timestamp = Instant.fromEpochSeconds(1727702654),
                    title = "Routine Check",
                    description = "Performed routine maintenance check.",
                    unit = "Unit 101",
                ),
                EventLogEntry(
                    id = EventLogEntryId("event456"),
                    employeeId = EmployeeId("emp789"),
                    fallbackEmployeeName = "Jane Doe",
                    propertyId = PropertyId("property101"),
                    type = EventLogEventType.MAINTENANCE_SERVICE,
                    fallbackEventType = "General Maintenance",
                    timestamp = Instant.fromEpochSeconds(1727702654),
                    title = "Routine Check",
                    description = "Performed routine maintenance check.",
                    unit = "Unit 101",
                ),
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

        // Act
        val response = client.get("event_log")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateEventLogEntry`() = testBackEndApplication {
        // Configure
        val requestBody = readFileContent("requests/update_event_log_entry_request.json")
        val expectedResponse = readFileContent("requests/update_event_log_entry_response.json")
        val userService = get<EventLogService>()
        val rbacService = get<RBACService>()
        coEvery {
            userService.updateEventLogEntry(
                id = EventLogEntryId("event123"),
                type = EventLogEventType.INCIDENT,
                fallbackEventType = "Inspection",
                title = "Monthly check",
                description = "Performed monthly inspection.",
                unit = "Unit 202",
            )
        }.answers {
            EventLogEntry(
                id = EventLogEntryId("event123"),
                employeeId = EmployeeId("emp456"),
                fallbackEmployeeName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.INCIDENT,
                fallbackEventType = "Inspection",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Monthly check",
                description = "Performed monthly inspection.",
                unit = "Unit 202",
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
            rbacService.hasRoleOrHigher(context, EventLogEntryId("event123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.put("event_log/event123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteEventLogEntry`() = testBackEndApplication {
        // Configure
        val userService = get<EventLogService>()
        val rbacService = get<RBACService>()
        coEvery {
            userService.deleteEventLogEntry(EventLogEntryId("event123"))
        }.answers {
            true
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
            rbacService.hasRoleOrHigher(context, EventLogEntryId("event123"), UserRole.EMPLOYEE)
        }.answers {
            true
        }

        // Act
        val response = client.delete("event_log/event123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
