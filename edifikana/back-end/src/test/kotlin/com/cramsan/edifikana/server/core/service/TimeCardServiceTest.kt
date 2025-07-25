package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test class for [TimeCardService].
 */
@OptIn(ExperimentalTime::class)
class TimeCardServiceTest {
    private lateinit var timeCardDatastore: TimeCardDatastore
    private lateinit var timeCardService: TimeCardService

    /**
     * Sets up the test environment by initializing mocks for [TimeCardDatastore] and [timeCardService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        timeCardDatastore = mockk()
        timeCardService = TimeCardService(timeCardDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createTimeCardEvent creates a time card event and returns it.
     */
    @Test
    fun `createTimeCardEvent should call database and return event`() = runTest {
        // Arrange
        val staffId = StaffId("staff-1")
        val fallbackStaffName = "John Doe"
        val propertyId = PropertyId("CENIT")
        val type = TimeCardEventType.CLOCK_IN
        val imageUrl = "http://image.url"
        val timestamp = Instant.parse("2024-06-18T12:00:00Z")
        val event = mockk<TimeCardEvent>()
        coEvery { timeCardDatastore.createTimeCardEvent(any()) } returns Result.success(event)

        // Act
        val result = timeCardService.createTimeCardEvent(
            staffId,
            fallbackStaffName,
            propertyId,
            type,
            imageUrl,
            timestamp
        )

        // Assert
        assertEquals(event, result)
        coVerify {
            timeCardDatastore.createTimeCardEvent(
                match {
                    it.staffId == staffId &&
                        it.fallbackStaffName == fallbackStaffName &&
                        it.propertyId == propertyId &&
                        it.type == type &&
                        it.imageUrl == imageUrl &&
                        it.timestamp == timestamp
                }
            )
        }
    }

    /**
     * Tests that getTimeCardEvent retrieves a time card event by ID and returns it.
     */
    @Test
    fun `getTimeCardEvent should call database and return event`() = runTest {
        // Arrange
        val eventId = TimeCardEventId("visitor")
        val event = mockk<TimeCardEvent>()
        coEvery { timeCardDatastore.getTimeCardEvent(any()) } returns Result.success(event)

        // Act
        val result = timeCardService.getTimeCardEvent(eventId)

        // Assert
        assertEquals(event, result)
        coVerify { timeCardDatastore.getTimeCardEvent(GetTimeCardEventRequest(eventId)) }
    }

    /**
     * Tests that getTimeCardEvent returns null if the event is not found.
     */
    @Test
    fun `getTimeCardEvent should return null if not found`() = runTest {
        // Arrange
        val eventId = TimeCardEventId("delivery")
        coEvery { timeCardDatastore.getTimeCardEvent(any()) } returns Result.failure(Exception("Not found"))

        // Act
        val result = timeCardService.getTimeCardEvent(eventId)

        // Assert
        assertNull(result)
        coVerify { timeCardDatastore.getTimeCardEvent(GetTimeCardEventRequest(eventId)) }
    }

    /**
     * Tests that getTimeCardEvents retrieves a list of time card events for a staff member.
     */
    @Test
    fun `getTimeCardEvents should call database and return list`() = runTest {
        // Arrange
        val staffId = StaffId("staff-3")
        val eventList = listOf(mockk<TimeCardEvent>(), mockk<TimeCardEvent>())
        coEvery { timeCardDatastore.getTimeCardEvents(any()) } returns Result.success(eventList)

        // Act
        val result = timeCardService.getTimeCardEvents(staffId)

        // Assert
        assertEquals(eventList, result)
        coVerify { timeCardDatastore.getTimeCardEvents(GetTimeCardEventListRequest(staffId)) }
    }
}
