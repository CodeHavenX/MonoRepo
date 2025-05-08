package com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import edifikana_lib.Res
import edifikana_lib.event_type_delivery
import edifikana_lib.event_type_guest
import edifikana_lib.title_event_log_view
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class ViewRecordViewModelTest : TestBase() {

    private lateinit var eventLogManager: EventLogManager
    private lateinit var storageService: StorageService
    private lateinit var viewModel: ViewRecordViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        eventLogManager = mockk()
        storageService = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        stringProvider = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = ViewRecordViewModel(
            eventLogManager,
            storageService,
            stringProvider = stringProvider,
            dependencies,
        )
    }

    @Test
    fun `test loadRecord successfully loads a record`() = runBlockingTest {
        // Arrange
        val recordId = EventLogEntryId("123")
        val record = EventLogRecordModel(
            id = recordId,
            title = "Test Record",
            description = "Test Description",
            eventType = EventLogEventType.DELIVERY,
            timeRecorded = 1234567890L,
            unit = "Unit",
            entityId = "Entity ID",
            staffPk = null,
            propertyId = PropertyId("Property ID"),
            fallbackStaffName = null,
            fallbackEventType = null,
            attachments = emptyList(),
        )
        coEvery { eventLogManager.getRecord(recordId) } returns Result.success(record)
        coEvery { stringProvider.getString(Res.string.title_event_log_view) } returns "View Record"
        coEvery { stringProvider.getString(Res.string.event_type_delivery) } returns "Delivery"

        // Act
        viewModel.loadRecord(recordId)

        // Assert
        coVerify { eventLogManager.getRecord(recordId) }
        val uiState = viewModel.uiState.value
        assertEquals(record.title, uiState.record?.title)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test loadRecord handles failure`() = runBlockingTest {
        // Arrange
        val recordId = EventLogEntryId("123")
        coEvery { eventLogManager.getRecord(recordId) } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_event_log_view) } returns "View Record"

        // Act
        viewModel.loadRecord(recordId)

        // Assert
        coVerify { eventLogManager.getRecord(recordId) }
        val uiState = viewModel.uiState.value
        assertEquals(null, uiState.record)
        assertEquals(false, uiState.isLoading)
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

    @Test
    fun `test openImage emits OpenImageExternally event`() = runBlockingTest {
        // Arrange
        val attachmentHolder = AttachmentHolder(publicUrl = "http://example.com/image.jpg", storageRef = "")
        coEvery { storageService.downloadFile(any()) } returns Result.success(CoreUri.createUri("http://example.com/image.jpg"))

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.OpenImageExternally(CoreUri.createUri("http://example.com/image.jpg")),
                    awaitItem(),
                )
            }
        }
        viewModel.openImage(attachmentHolder)

        // Assert
        verificationJob.join()
    }
}