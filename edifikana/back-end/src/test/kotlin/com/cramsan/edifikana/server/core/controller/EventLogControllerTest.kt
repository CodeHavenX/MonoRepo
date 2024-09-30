package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.EventLogEntryId
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.utils.readFileContent
import com.cramsan.framework.test.TestBase
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.coEvery
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Instant
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class EventLogControllerTest : TestBase(), KoinTest {

    /**
     * Setup the test.
     */
    override fun setupTest() = Unit

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
        startTestKoin()
        val requestBody = readFileContent("requests/create_event_log_entry_request.json")
        val expectedResponse = readFileContent("requests/create_event_log_entry_response.json")
        val userService = get<EventLogService>()
        coEvery {
            userService.createEventLog(
                StaffId("test"),
                Instant.fromEpochMilliseconds(1727702654),
                "test event",
            )
        }.answers {
            EventLogEntry(
                id = EventLogEntryId("test"),
                staffId = StaffId("test"),
                time = Instant.fromEpochMilliseconds(1727702654),
                title = "test event",
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
}