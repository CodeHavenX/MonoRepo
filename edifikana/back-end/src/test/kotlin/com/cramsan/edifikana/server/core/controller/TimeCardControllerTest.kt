package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.test.TestBase
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeCardControllerTest : TestBase(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin()
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test createTimeCardEvent`() = testEdifikanaApplication {
        // Configure
        val requestBody = readFileContent("requests/create_timecard_event_request.json")
        val expectedResponse = readFileContent("requests/create_timecard_event_response.json")
        val timeCardService = get<TimeCardService>()
        val clock = get<Clock>()

        coEvery {
            timeCardService.createTimeCardEvent(
                staffId = StaffId("staff123"),
                fallbackStaffName = "John Doe",
                propertyId = PropertyId("property123"),
                type = TimeCardEventType.CLOCK_OUT,
                imageUrl = "http://example.com/image.jpg",
                timestamp = Instant.fromEpochSeconds(1727702654),
            )
        }.answers {
            TimeCardEvent(
                id = TimeCardEventId("timecard123"),
                staffId = StaffId("staff123"),
                fallbackStaffName = "John Doe",
                propertyId = PropertyId("property123"),
                type = TimeCardEventType.CLOCK_OUT,
                imageUrl = "http://example.com/image.jpg",
                timestamp = Instant.fromEpochSeconds(1727702654),
            )
        }
        every { clock.now() } returns Instant.fromEpochSeconds(1727702654)
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
        val response = client.post("time_card") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getTimeCardEvent`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_timecard_event_response.json")
        val timeCardService = get<TimeCardService>()
        coEvery {
            timeCardService.getTimeCardEvent(TimeCardEventId("timecard123"))
        }.answers {
            TimeCardEvent(
                id = TimeCardEventId("timecard123"),
                staffId = StaffId("staff123"),
                fallbackStaffName = "John Doe",
                propertyId = PropertyId("property123"),
                type = TimeCardEventType.CLOCK_IN,
                imageUrl = "http://example.com/image.jpg",
                timestamp = Instant.fromEpochSeconds(1727702654),
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
        val response = client.get("time_card/timecard123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getTimeCardEvents`() = testEdifikanaApplication {
        // Configure
        val expectedResponse = readFileContent("requests/get_timecard_events_response.json")
        val timeCardService = get<TimeCardService>()
        coEvery {
            timeCardService.getTimeCardEvents(null)
        }.answers {
            listOf(
                TimeCardEvent(
                    id = TimeCardEventId("timecard123"),
                    staffId = StaffId("staff123"),
                    fallbackStaffName = "John Doe",
                    propertyId = PropertyId("property123"),
                    type = TimeCardEventType.CLOCK_IN,
                    imageUrl = "http://example.com/image.jpg",
                    timestamp = Instant.fromEpochSeconds(1727702654),
                ),
                TimeCardEvent(
                    id = TimeCardEventId("timecard456"),
                    staffId = StaffId("staff456"),
                    fallbackStaffName = "Jane Smith",
                    propertyId = PropertyId("property456"),
                    type = TimeCardEventType.CLOCK_IN,
                    imageUrl = "http://example.com/image2.jpg",
                    timestamp = Instant.fromEpochSeconds(1727702654),
                )
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
        val response = client.get("time_card")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
