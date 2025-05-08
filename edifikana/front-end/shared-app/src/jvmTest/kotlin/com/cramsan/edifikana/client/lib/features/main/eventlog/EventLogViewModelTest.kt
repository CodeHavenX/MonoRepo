package com.cramsan.edifikana.client.lib.features.main.eventlog

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class EventLogViewModelTest : TestBase() {

    private lateinit var eventLogManager: EventLogManager
    private lateinit var viewModel: EventLogViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        eventLogManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = EventLogViewModel(eventLogManager, dependencies)
    }

    @Test
    fun `test loadRecords successfully loads records`() = runBlockingTest {
        // Arrange
        val records = listOf(
            EventLogRecordModel(
                id = EventLogEntryId("1"),
                title = "Record 1",
                description = "Description 1",
                eventType = EventLogEventType.DELIVERY,
                timeRecorded = 23524543,
                unit = "Unit 1",
                entityId = "Entity 1",
                staffPk = StaffId("Staff 1"),
                propertyId = PropertyId("Property 1"),
                fallbackStaffName = null,
                fallbackEventType = null,
                attachments = emptyList()
            ),
            EventLogRecordModel(
                id = EventLogEntryId("2"),
                title = "Record 2",
                description = "Description 2",
                eventType = EventLogEventType.GUEST,
                timeRecorded = 32523532126,
                unit = "Unit 2",
                entityId = "Entity 2",
                staffPk = StaffId("Staff 2"),
                propertyId = PropertyId("Property 2"),
                fallbackStaffName = null,
                fallbackEventType = null,
                attachments = emptyList(),
            )
        )
        coEvery { eventLogManager.getRecords() } returns Result.success(records)

        // Act
        viewModel.loadRecords()

        // Assert
        coVerify { eventLogManager.getRecords() }
        val uiState = viewModel.uiState.value
        assertEquals(2, uiState.records.size)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test loadRecords handles failure`() = runBlockingTest {
        // Arrange
        coEvery { eventLogManager.getRecords() } returns Result.failure(Exception("Error"))

        // Act
        viewModel.loadRecords()

        // Assert
        coVerify { eventLogManager.getRecords() }
        val uiState = viewModel.uiState.value
        assertEquals(0, uiState.records.size)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test openRecordScreen emits NavigateToScreen event`() = runBlockingTest {
        // Arrange
        val recordId = EventLogEntryId("123")

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToScreen(
                        ManagementDestination.EventLogSingleItemDestination(recordId)
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.openRecordScreen(recordId)

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test openAddRecordScreen emits NavigateToScreen event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToScreen(
                        ManagementDestination.EventLogAddItemDestination
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.openAddRecordScreen()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateBack,
                    awaitItem(),
                )
            }
        }
        viewModel.navigateBack()

        // Assert
        verificationJob.join()
    }
}