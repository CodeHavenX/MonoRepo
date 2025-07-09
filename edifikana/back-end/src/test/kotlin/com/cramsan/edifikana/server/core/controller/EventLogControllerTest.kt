package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.utils.readFileContent
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
        startTestKoin()
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
    fun `test createEventLog`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_event_log_entry_request.json")
        val expectedResponse = readFileContent("requests/create_event_log_entry_response.json")
        val userService = get<EventLogService>()
        coEvery {
            userService.createEventLogEntry(
                staffId = StaffId("staff456"),
                fallbackStaffName = "John Doe",
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
                staffId = StaffId("staff456"),
                fallbackStaffName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.MAINTENANCE_SERVICE,
                fallbackEventType = "General Maintenance",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Routine Check",
                description = "Performed routine maintenance check.",
                unit = "Unit 101",
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
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
    fun `test getEventLogEntry`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_event_log_entry_response.json")
        val userService = get<EventLogService>()
        coEvery {
            userService.getEventLogEntry(
                EventLogEntryId("event123"),
            )
        }.answers {
            EventLogEntry(
                id = EventLogEntryId("event123"),
                staffId = StaffId("staff456"),
                fallbackStaffName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.MAINTENANCE_SERVICE,
                fallbackEventType = "General Maintenance",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Routine Check",
                description = "Performed routine maintenance check.",
                unit = "Unit 101",
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.get("event_log/event123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getEventLogEntries`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_event_log_entries_response.json")
        val userService = get<EventLogService>()
        coEvery {
            userService.getEventLogEntries()
        }.answers {
            listOf(
                EventLogEntry(
                    id = EventLogEntryId("event123"),
                    staffId = StaffId("staff456"),
                    fallbackStaffName = "John Doe",
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
                    staffId = StaffId("staff789"),
                    fallbackStaffName = "Jane Doe",
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
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.get("event_log")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateEventLogEntry`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/update_event_log_entry_request.json")
        val expectedResponse = readFileContent("requests/update_event_log_entry_response.json")
        val userService = get<EventLogService>()
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
                staffId = StaffId("staff456"),
                fallbackStaffName = "John Doe",
                propertyId = PropertyId("property789"),
                type = EventLogEventType.INCIDENT,
                fallbackEventType = "Inspection",
                timestamp = Instant.fromEpochSeconds(1727702654),
                title = "Monthly check",
                description = "Performed monthly inspection.",
                unit = "Unit 202",
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
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
    fun `test deleteEventLogEntry`() = testEdifikanaApplication {
        // Configure
        val userService = get<EventLogService>()
        coEvery {
            userService.deleteEventLogEntry(EventLogEntryId("event123"))
        }.answers {
            true
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        }

        // Act
        val response = client.delete("event_log/event123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
