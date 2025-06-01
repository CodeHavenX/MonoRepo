package com.cramsan.edifikana.client.lib.features.main.eventlog.addrecord

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.dummy.STAFF_1
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.annotations.TestOnly
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.utils.time.Chronos
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class, TestOnly::class)
class AddRecordViewModelTest : TestBase() {

    private lateinit var eventLogManager: EventLogManager
    private lateinit var staffManager: StaffManager
    private lateinit var viewModel: AddRecordViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var clock: Clock
    private lateinit var propertyManager: PropertyManager
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        clock = mockk()
        staffManager = mockk()
        eventLogManager = mockk()
        propertyManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = AddRecordViewModel(
            staffManager,
            eventLogManager,
            propertyManager,
            stringProvider = stringProvider,
            dependencies,
        )
        Chronos.initializeClock(clock)
    }

    @AfterTest
    fun tearDown() {
        Chronos.clear()
    }

    @Test
    fun `test addRecord successfully adds a record`() = runBlockingTest {
        // Arrange
        val record = EventLogRecordModel(
            id = null,
            staffPk = StaffId("staff_id_1"),
            fallbackStaffName = null,
            propertyId = PropertyId("property_id_1"),
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            fallbackEventType = null,
            timeRecorded = 1727702654,
            title = "Routine Check",
            description = "Performed routine maintenance check.",
            unit = "Unit 101",
            entityId = "staff_id_1_1727702654",
            attachments = emptyList(),
        )
        every { clock.now() } returns Instant.fromEpochSeconds(1727702654)
        coEvery { eventLogManager.addRecord(record) } returns Result.success(Unit)
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(record.propertyId)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem(),
                )
            }
        }
        viewModel.addRecord(
            staffDocumentId = STAFF_1.id,
            unit = "Unit 101",
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            fallbackStaffName = null,
            fallbackEventType = null,
            title = "Routine Check",
            description = "Performed routine maintenance check.",
        ).join()

        // Assert
        coVerify { eventLogManager.addRecord(record) }
        val uiState = viewModel.uiState.value
        assertEquals(true, uiState.isLoading)
        verificationJob.join()
    }

    @Test
    fun `test addRecord handles failure`() = runBlockingTest {
        // Arrange
        val record = EventLogRecordModel(
            id = null,
            staffPk = StaffId("staff_id_1"),
            fallbackStaffName = null,
            propertyId = PropertyId("property_id_1"),
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            fallbackEventType = null,
            timeRecorded = 1727702654,
            title = "Routine Check",
            description = "Performed routine maintenance check.",
            unit = "Unit 101",
            entityId = "staff_id_1_1727702654",
            attachments = emptyList(),
        )
        every { clock.now() } returns Instant.fromEpochSeconds(1727702654)
        coEvery { eventLogManager.addRecord(record) } returns Result.failure(Exception("Error"))
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(record.propertyId)

        // Act
        viewModel.addRecord(
            staffDocumentId = STAFF_1.id,
            unit = "Unit 101",
            eventType = EventLogEventType.MAINTENANCE_SERVICE,
            fallbackStaffName = null,
            fallbackEventType = null,
            title = "Routine Check",
            description = "Performed routine maintenance check.",
        ).join()

        // Assert
        coVerify { eventLogManager.addRecord(record) }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(null, uiState.records.firstOrNull())
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem(),
                )
            }
        }
        viewModel.navigateBack()

        // Assert
        verificationJob.join()
    }
}