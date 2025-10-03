package com.cramsan.edifikana.client.lib.features.main.timecard.viewemployee

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.management.viewemployee.ViewEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole

import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.annotations.TestOnly
import com.cramsan.framework.core.CoreUri
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
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.time.Chronos
import edifikana_lib.Res
import edifikana_lib.role_security
import edifikana_lib.time_card_event_clock_in
import edifikana_lib.time_card_event_clock_out
import edifikana_lib.title_timecard_view_employee
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.BeforeEach
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(TestOnly::class, ExperimentalTime::class)
class ViewEmployeeViewModelTest : CoroutineTest() {

    private lateinit var employeeManager: EmployeeManager
    private lateinit var timeCardManager: TimeCardManager
    private lateinit var storageService: StorageService
    private lateinit var propertyManager: PropertyManager
    private lateinit var viewModel: ViewEmployeeViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        employeeManager = mockk()
        timeCardManager = mockk()
        storageService = mockk()
        propertyManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        stringProvider = mockk()
        viewModel = ViewEmployeeViewModel(
            employeeManager,
            timeCardManager,
            storageService,
            propertyManager,
            stringProvider = stringProvider,
            dependencies,
        )
        Chronos.initializeClock(mockk())
        Chronos.setTimeZoneOverride(TimeZone.UTC)
    }

    @AfterTest
    fun tearDown() {
        Chronos.clear()
    }

    @Test
    fun `test loadEmployee successfully loads employee and records`() = runCoroutineTest {
        // Arrange
        val employeeId = EmployeeId("123")
        val employee = EmployeeModel(
            id = employeeId,
            idType = IdType.PASSPORT,
            firstName = "John",
            lastName = "Doe",
            role = EmployeeRole.SECURITY,
            email = "john.doe@test.com",
        )
        val records = listOf(
            TimeCardRecordModel(
                id = TimeCardEventId("1"),
                employeePk = employeeId,
                eventType = TimeCardEventType.CLOCK_IN,
                propertyId = PropertyId("propertyId"),
                eventTime = 23524543,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
                entityId = "entityId",
            ),
            TimeCardRecordModel(
                id = TimeCardEventId("2"),
                employeePk = employeeId,
                eventType = TimeCardEventType.CLOCK_OUT,
                propertyId = PropertyId("propertyId"),
                eventTime = 32523532126,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
                entityId = "entityId",
            )
        )
        coEvery { employeeManager.getEmployee(employeeId) } returns Result.success(employee)
        coEvery { timeCardManager.getRecords(employeeId) } returns Result.success(records)
        coEvery { stringProvider.getString(Res.string.title_timecard_view_employee) } returns "View Employee"
        coEvery { stringProvider.getString(Res.string.role_security) } returns "Security"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_out) } returns "Clock Out"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_in) } returns "Clock In"

        // Act
        viewModel.loadEmployee(employeeId)

        // Assert
        coVerify { employeeManager.getEmployee(employeeId) }
        coVerify { timeCardManager.getRecords(employeeId) }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("John Doe", uiState.employee?.fullName)
        assertEquals(2, uiState.records.size)
    }

    @Test
    fun `test loadEmployee handles failure`() = runCoroutineTest {
        // Arrange
        val employeeId = EmployeeId("123")
        coEvery { employeeManager.getEmployee(employeeId) } returns Result.failure(Exception("Error"))
        coEvery { timeCardManager.getRecords(employeeId) } returns Result.failure(Exception("Error"))

        // Act
        viewModel.loadEmployee(employeeId)

        // Assert
        coVerify { employeeManager.getEmployee(employeeId) }
        coVerify { timeCardManager.getRecords(employeeId) }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("", uiState.employee?.fullName)
        assertEquals(0, uiState.records.size)
    }

    @Test
    fun `test share emits ShareContent event`() = runCoroutineTest {
        // Arrange
        val timeCardEventId = TimeCardEventId("1")
        val record = TimeCardRecordModel(
            id = timeCardEventId,
            employeePk = EmployeeId("123"),
            eventType = TimeCardEventType.CLOCK_IN,
            propertyId = PropertyId("propertyId"),
            eventTime = 23524543,
            imageUrl = "imageUrl",
            imageRef = "imageRef",
            entityId = "entityId",
        )
        val employee = EmployeeModel(
            id = EmployeeId("123"),
            idType = IdType.PASSPORT,
            firstName = "John",
            lastName = "Doe",
            role = EmployeeRole.SECURITY,
            email = "john.doe@test.com",
        )
        coEvery { storageService.downloadFile(any()) } returns Result.success(CoreUri.createUri("http://example.com/image.jpg"))
        coEvery { employeeManager.getEmployee(any()) } returns Result.success(employee)
        coEvery { timeCardManager.getRecords(any()) } returns Result.success(listOf(record))
        coEvery { stringProvider.getString(Res.string.title_timecard_view_employee) } returns "View Employee"
        coEvery { stringProvider.getString(Res.string.role_security) } returns "Security"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_out) } returns "Clock-Out"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_in) } returns "Clock-In"

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShareContent(
                        "Clock-In de John Doe\n1970-09-30T06:35:43",
                        CoreUri.createUri("http://example.com/image.jpg")
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.loadEmployee(EmployeeId("123")).join()
        viewModel.share(timeCardEventId).join()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                viewModel.navigateBack()
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }

        // Assert
        verificationJob.join()
    }
}