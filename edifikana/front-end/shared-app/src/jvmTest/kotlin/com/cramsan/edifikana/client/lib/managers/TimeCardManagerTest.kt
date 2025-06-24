package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.TimeCardCache
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the TimeCardManager class.
 */
class TimeCardManagerTest : TestBase() {
    private lateinit var timeCardService: TimeCardService
    private lateinit var timeCardCache: TimeCardCache
    private lateinit var storageService: StorageService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var ioDependencies: IODependencies
    private lateinit var manager: TimeCardManager

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        timeCardService = mockk()
        timeCardCache = mockk()
        storageService = mockk()

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        ioDependencies = mockk()
        manager = TimeCardManager(timeCardService, timeCardCache, storageService, dependencies, ioDependencies)
    }

    /**
     * Tests that getRecords merges and sorts records from cache and service.
     */
    @Test
    fun `getRecords returns merged and sorted records`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("staff-1")
        val cached = listOf(
            TimeCardRecordModel(
                TimeCardEventId("1"),
                "testId",
                staffId,
                PropertyId("Muralla"),
                TimeCardEventType.CLOCK_IN,
                100,
                null,
                null,
            ),
            TimeCardRecordModel(
                TimeCardEventId("2"),
                "testId2",
                staffId,
                PropertyId("Muralla"),
                TimeCardEventType.CLOCK_OUT,
                400,
                null,
                null,
                )
        )
        val online = listOf(
            TimeCardRecordModel(
                TimeCardEventId("3"),
                "testId3",
                staffId,
                PropertyId("Muralla"),
                TimeCardEventType.CLOCK_IN,
                500,
                null,
                null,
                )
        )
        coEvery { timeCardCache.getRecords(staffId) } returns cached
        coEvery { timeCardService.getRecords(staffId) } returns Result.success(online)

        // Act
        val result = manager.getRecords(staffId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(listOf(online[0], cached[1], cached[0]), result.getOrNull())
    }

    /**
     * Tests that getAllRecords merges and sorts records from cache and service.
     */
    @Test
    fun `getAllRecords returns merged and sorted records`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("staff-X")

        val cached = listOf(
            TimeCardRecordModel(
                TimeCardEventId("1"),
                "testId3",
                staffId,
                PropertyId("Muralla"),
                TimeCardEventType.CLOCK_IN,
                100,
                "imageUrl1",
                "imagerRef1")
        )
        val online = listOf(
            TimeCardRecordModel(
                TimeCardEventId("2"),
                "testId3",
                staffId,
                PropertyId("Cenit"),
                TimeCardEventType.CLOCK_OUT,
                200,
                "imagerUrl2",
                "imagerRef2")
        )
        coEvery { timeCardCache.getAllRecords() } returns cached
        coEvery { timeCardService.getAllRecords() } returns Result.success(online)


        // Act
        val result = manager.getAllRecords()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(listOf(online[0], cached[0]), result.getOrNull())
    }

    /**
     * Tests that getRecord retrieves a specific record from the service.
     */
    @Test
    fun `getRecord returns record from service`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("staff-Y")
        val eventId = TimeCardEventId("event-X")
        val record = TimeCardRecordModel(
            eventId,
            "testId4",
            staffId,
            PropertyId("Barranco"),
            TimeCardEventType.CLOCK_OUT,
            123,
            "imagerUrl3",
            "imagerRef3")
        coEvery { timeCardService.getRecord(eventId) } returns Result.success(record)


        // Act
        val result = manager.getRecord(eventId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(record, result.getOrNull())
    }

    /**
     * Tests that addRecord adds to cache and triggers upload.
     */
    @Test
    fun `addRecord adds to cache and triggers upload`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("staff-Z")
        val record = TimeCardRecordModel(TimeCardEventId(
            "event-Y"),
            "testId4",
            staffId,
            PropertyId("Cenit"),
            TimeCardEventType.CLOCK_IN,
            456,
            "imagerUrl4",
            "imagerRef4")
        val uri = mockk<CoreUri>()
        coEvery { timeCardCache.addRecord(record, uri) } returns Unit

        // Act
        val result = manager.addRecord(record, uri)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { timeCardCache.addRecord(record, uri) }
    }

    // Test data for time card records
    val staffIdTest1 = StaffId("staff-Z")
    val staffIdTest2 = StaffId("staff-Y")
    val eventIdTest1 = TimeCardEventId("event-X")
    val eventIdTest2 = TimeCardEventId("event-Y")
    val eventIdTest3 = TimeCardEventId("event-Z")

    val timeCardRecordTest1: TimeCardRecordModel = TimeCardRecordModel(
        eventIdTest1,
        "testId",
        staffIdTest1,
        PropertyId("test-property-id"),
        TimeCardEventType.CLOCK_IN,
        456,
        "test-image-url",
        "test-image-ref"
    )
    val timeCardRecordTest2 = TimeCardRecordModel(
        eventIdTest1,
        "testId4",
        staffIdTest1,
        PropertyId("Barranco"),
        TimeCardEventType.CLOCK_OUT,
        123,
        "test-image-url",
        "test-image-ref")

    val timeCardRecordTest3 = TimeCardRecordModel(
        eventIdTest2,
        "testId3",
        staffIdTest1,
        PropertyId("Cenit"),
        TimeCardEventType.CLOCK_OUT,
        200,
        "test-image-url",
        "test-image-ref")

    val timeCardRecordTest4 = TimeCardRecordModel(
        eventIdTest3,
        "testId3",
        staffIdTest2,
        PropertyId("Muralla"),
        TimeCardEventType.CLOCK_IN,
        100,
        "test-image-url",
        "test-image-ref")
}

