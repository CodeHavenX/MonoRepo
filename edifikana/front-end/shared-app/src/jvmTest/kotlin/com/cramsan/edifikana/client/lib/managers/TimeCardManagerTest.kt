package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.TimeCardCache
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.CoreUri
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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the TimeCardManager class.
 */
class TimeCardManagerTest : CoroutineTest() {
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
     * Tests that getRecords merges cached and online records, sorts them by event time,
     * and returns the result.
     */
    @Test
    fun `getRecords returns merged and sorted records`() = runCoroutineTest {
        // Arrange
        val cached = listOf(
            timeCardRecordTest1,
            timeCardRecordTest2,
        )
        val online = listOf(
            timeCardRecordTest3
        )
        coEvery { timeCardCache.getRecords(employeeIdTest) } returns cached
        coEvery { timeCardService.getRecords(employeeIdTest) } returns Result.success(online)

        // Act
        val result = manager.getRecords(employeeIdTest)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(listOf(online[0], cached[1], cached[0]), result.getOrNull())
    }

    /**
     * Tests that getAllRecords merges cached and online records, sorts them by event time,
     * and returns the result.
     */
    @Test
    fun `getAllRecords returns merged and sorted records`() = runCoroutineTest {
        // Arrange
        val cached = listOf(
            timeCardRecordTest1
        )
        val online = listOf(
            timeCardRecordTest4
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
    fun `getRecord returns record from service`() = runCoroutineTest {
        // Arrange
        val record = timeCardRecordTest2
        coEvery { timeCardService.getRecord(eventIdCachedTest2) } returns Result.success(record)


        // Act
        val result = manager.getRecord(eventIdCachedTest2)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(record, result.getOrNull())
    }

    /**
     * Tests that addRecord adds the record to the cache and triggers an upload.
     * TODO: ENABLE ONCE FUNCTIONALITY IMPLEMENTED
     */
    @Ignore
    @Test
    fun `addRecord adds to cache and triggers upload`() = runCoroutineTest {
        // Arrange
        val record = timeCardRecordTest3
        val uri = mockk<CoreUri>()
        coEvery { timeCardCache.addRecord(record, uri) } returns Unit

        // Act
        val result = manager.addRecord(record, uri)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { timeCardCache.addRecord(record, uri) }
    }

    // Test data for time card records
    val employeeIdTest = EmployeeId("employee-X")
    val eventIdCachedTest1 = TimeCardEventId("event-X")
    val eventIdCachedTest2 = TimeCardEventId("event-Y")
    val eventIdOnlineTest3 = TimeCardEventId("event-Z")

    val timeCardRecordTest1: TimeCardRecordModel = TimeCardRecordModel(
        eventIdCachedTest1,
        "testId",
        employeeIdTest,
        PropertyId("Muralla"),
        TimeCardEventType.CLOCK_IN,
        100,
        "test-image-url",
        "test-image-ref"
    )
    val timeCardRecordTest2 = TimeCardRecordModel(
        eventIdCachedTest2,
        "testId2",
        employeeIdTest,
        PropertyId("Muralla"),
        TimeCardEventType.CLOCK_OUT,
        400,
        "test-image-url",
        "test-image-ref")

    val timeCardRecordTest3 = TimeCardRecordModel(
        eventIdOnlineTest3,
        "testId3",
        employeeIdTest,
        PropertyId("Muralla"),
        TimeCardEventType.CLOCK_IN,
        500,
        "test-image-url",
        "test-image-ref")

    val timeCardRecordTest4 = TimeCardRecordModel(
        eventIdOnlineTest3,
        "testId4",
        employeeIdTest,
        PropertyId("Muralla"),
        TimeCardEventType.CLOCK_OUT,
        800,
        "test-image-url",
        "test-image-ref")
}

