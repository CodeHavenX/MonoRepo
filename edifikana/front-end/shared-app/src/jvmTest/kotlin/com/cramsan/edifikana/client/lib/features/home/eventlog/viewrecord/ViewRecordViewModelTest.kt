package com.cramsan.edifikana.client.lib.features.home.eventlog.viewrecord

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import edifikana_lib.Res
import edifikana_lib.event_type_delivery
import edifikana_lib.title_event_log_view
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class ViewRecordViewModelTest : CoroutineTest() {

    private lateinit var eventLogManager: EventLogManager
    private lateinit var storageService: StorageService
    private lateinit var viewModel: ViewRecordViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var stringProvider: StringProvider
    private lateinit var delegatedEventEmitter: EventEmitter<EdifikanaWindowDelegatedEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        eventLogManager = mockk()
        storageService = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        delegatedEventEmitter = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = ViewRecordViewModel(
            eventLogManager,
            storageService,
            stringProvider = stringProvider,
            delegatedEventEmitter,
            dependencies,
        )
    }

    @Test
    fun `test loadRecord successfully loads a record`() = runCoroutineTest {
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
            employeePk = null,
            propertyId = PropertyId("Property ID"),
            fallbackEmployeeName = null,
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
    fun `test loadRecord handles failure`() = runCoroutineTest {
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
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
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

    @Test
    fun `test openImage emits OpenImageExternally event`() = runCoroutineTest {
        // Arrange
        val attachmentHolder = AttachmentHolder(publicUrl = "http://example.com/image.jpg", storageRef = "")
        coEvery { storageService.downloadFile(any()) } returns Result.success(CoreUri.createUri("http://example.com/image.jpg"))

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.OpenImageExternally(CoreUri.createUri("http://example.com/image.jpg")),
                    awaitItem(),
                )
            }
        }
        viewModel.openImage(attachmentHolder)

        // Assert
        verificationJob.join()
    }
}