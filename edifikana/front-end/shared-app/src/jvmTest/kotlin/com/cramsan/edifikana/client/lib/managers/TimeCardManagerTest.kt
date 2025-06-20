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
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimeCardManagerTest {
    private lateinit var timeCardService: TimeCardService
    private lateinit var timeCardCache: TimeCardCache

    private lateinit var storageService: StorageService

    private lateinit var dependencies: ManagerDependencies

    private lateinit var ioDependencies: IODependencies

    private lateinit var manager: TimeCardManager

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        timeCardService = mockk()
        timeCardCache = mockk()
        storageService = mockk()
        dependencies = mockk(relaxed = true)
        ioDependencies = mockk()
        manager = TimeCardManager(timeCardService, timeCardCache, storageService, dependencies, ioDependencies)
    }

    @Test
    fun `getRecords returns merged and sorted records`() = runTest {
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
                400,
                null,
                null,
                )
        )
        coEvery { timeCardCache.getRecords(staffId) } returns cached
        coEvery { timeCardService.getRecords(staffId) } returns Result.success(online)
        coEvery { dependencies.getOrCatch<List<TimeCardRecordModel>>(any(), any()) } answers {
            val block = args[1] as suspend () -> List<TimeCardRecordModel>
            block()
        }

        val result = manager.getRecords(staffId)
        assertTrue(result.isSuccess)
        assertEquals(listOf(online[0], cached[1], cached[0]], result.getOrNull())
    }

    @Test
    fun `getAllRecords returns merged and sorted records`() = runTest {
        val cached = listOf(
            TimeCardRecordModel(TimeCardEventId("1"), eventTime = 100)
        )
        val online = listOf(
            TimeCardRecordModel(TimeCardEventId("2"), eventTime = 200)
        )
        coEvery { timeCardCache.getAllRecords() } returns cached
        coEvery { timeCardService.getAllRecords() } returns Result.success(online)
        coEvery { dependencies.getOrCatch<List<TimeCardRecordModel>>(any(), any()) } answers {
            val block = args[1] as suspend () -> List<TimeCardRecordModel>
            block()
        }

        val result = manager.getAllRecords()
        assertTrue(result.isSuccess)
        assertEquals(listOf(online[0], cached[0]), result.getOrNull())
    }

    @Test
    fun `getRecord returns record from service`() = runTest {
        val eventId = TimeCardEventId("event-1")
        val record = TimeCardRecordModel(eventId, eventTime = 123)
        coEvery { timeCardService.getRecord(eventId) } returns Result.success(record)
        coEvery { dependencies.getOrCatch<TimeCardRecordModel>(any(), any()) } answers {
            val block = args[1] as suspend () -> TimeCardRecordModel
            block()
        }

        val result = manager.getRecord(eventId)
        assertTrue(result.isSuccess)
        assertEquals(record, result.getOrNull())
    }

    @Test
    fun `addRecord adds to cache and triggers upload`() = runTest {
        val record = TimeCardRecordModel(TimeCardEventId("event-2"), eventTime = 456)
        val uri = mockk<CoreUri>()
        coEvery { timeCardCache.addRecord(record, uri) } returns Unit
        coEvery { dependencies.getOrCatch<Unit>(any(), any()) } answers {
            val block = args[1] as suspend () -> Unit
            block()
        }
        coEvery { manager.uploadRecord(record) } returns Result.success(Unit)
        coEvery { manager.triggerFullUpload() } returns Unit

        val result = manager.addRecord(record, uri)
        assertTrue(result.isSuccess)
        coVerify { timeCardCache.addRecord(record, uri) }
    }
}

