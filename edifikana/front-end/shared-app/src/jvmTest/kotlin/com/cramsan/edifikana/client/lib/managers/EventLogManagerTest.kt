package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.EventLogCache
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * Unit tests for the EventLogManager class.
 */
class EventLogManagerTest : CoroutineTest() {
    private lateinit var eventLogService: EventLogService
    private lateinit var eventLogCache: EventLogCache
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: EventLogManager

    /**
     * Sets up the test environment, initializing mocks and the EventLogManager instance.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        eventLogService = mockk()
        eventLogCache = mockk()
        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = EventLogManager(eventLogService, eventLogCache, dependencies)
    }

    /**
     * Tests that getRecords merges and sorts records from cache and service.
     */
    @Test
    fun `getRecords returns merged and sorted records`() = runTest {
        // Arrange
        val cached = listOf(eventTest1, eventTest2)
        val online = listOf(eventTest3)
        val propertyId = PropertyId("Cenit")
        coEvery { eventLogCache.getRecords() } returns cached
        coEvery { eventLogService.getRecords(propertyId) } returns Result.success(online)
        // Act
        val result = manager.getRecords(propertyId)
        // Assert
        assertTrue(result.isSuccess)
        val merged = result.getOrNull()!!
        assertEquals(3, merged.size)
        assertEquals(listOf("incident2", "maintenance1", "incident1"), merged.map { it.id.toString() })
        coVerify { eventLogCache.getRecords() }
        coVerify { eventLogService.getRecords(propertyId) }
    }

    /**
     * Tests that getRecord returns the record from the service.
     */
    @Test
    fun `getRecord returns record`() = runTest {
        // Arrange
        val id = EventLogEntryId("incident1")
        coEvery { eventLogService.getRecord(id) } returns Result.success(eventTest1)
        // Act
        val result = manager.getRecord(id)
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(eventTest1, result.getOrNull())
        coVerify { eventLogService.getRecord(id) }
    }

    // events for testing
    val eventTest1: EventLogRecordModel = EventLogRecordModel(
        id = EventLogEntryId("incident1"),
        entityId = "entity1",
        employeePk = null,
        propertyId = PropertyId("Cenit"),
        timeRecorded = 1000L,
        unit = "unit1",
        eventType = EventLogEventType.INCIDENT,
        fallbackEmployeeName = null,
        fallbackEventType = null,
        title = "Major Incident",
        description = "Description for test event 1",
        attachments = emptyList()
    )

    val eventTest2: EventLogRecordModel = EventLogRecordModel(
        id = EventLogEntryId("maintenance1"),
        entityId = "entity2",
        employeePk = null,
        propertyId = PropertyId("Cenit"),
        timeRecorded = 2000L,
        unit = "unit2",
        eventType = EventLogEventType.MAINTENANCE_SERVICE,
        fallbackEmployeeName = null,
        fallbackEventType = null,
        title = "Routine Maintenance",
        description = "Description for test event 2",
        attachments = emptyList()
    )

    val eventTest3: EventLogRecordModel = EventLogRecordModel(
        id = EventLogEntryId("incident2"),
        entityId = "entity3",
        employeePk = null,
        propertyId = PropertyId("Cenit"),
        timeRecorded = 3000L,
        unit = "unit3",
        eventType = EventLogEventType.INCIDENT,
        fallbackEmployeeName = null,
        fallbackEventType = null,
        title = "Minor Incident",
        description = "Description for test event 3",
        attachments = emptyList()
    )
}