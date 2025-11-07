package com.cramsan.edifikana.client.lib.features.home.timecard

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.home.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole

import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
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
import edifikana_lib.Res
import edifikana_lib.time_card_event_clock_in
import edifikana_lib.time_card_event_clock_out
import edifikana_lib.title_timecard
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeCartViewModelTest : CoroutineTest() {

    private lateinit var timeCardManager: TimeCardManager
    private lateinit var employeeManager: EmployeeManager
    private lateinit var viewModel: TimeCartViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        timeCardManager = mockk()
        employeeManager = mockk()
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
        viewModel = TimeCartViewModel(
            timeCardManager,
            employeeManager,
            stringProvider = stringProvider,
            dependencies,
        )
    }

    @Test
    fun `test loadEvents successfully loads events`() = runCoroutineTest {
        // Arrange
        val events = listOf(
            TimeCardRecordModel(
                id = TimeCardEventId("1"),
                employeePk = EmployeeId("1"),
                eventType = TimeCardEventType.CLOCK_IN,
                entityId = "entityId",
                propertyId = PropertyId("propertyId"),
                eventTime = 23524543,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
            ),
            TimeCardRecordModel(
                id = TimeCardEventId("2"),
                employeePk = EmployeeId("2"),
                eventType = TimeCardEventType.CLOCK_OUT,
                entityId = "entityId",
                propertyId = PropertyId("propertyId"),
                eventTime = 32523532126,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
            )
        )
        val employees = listOf(
            EmployeeModel(
                id = EmployeeId("1"),
                idType = IdType.PASSPORT,
                firstName = "John",
                lastName = "Doe",
                role = EmployeeRole.SECURITY,
                email = "johndoe@test.com",
            ),
            EmployeeModel(
                id = EmployeeId("2"),
                idType = IdType.PASSPORT,
                firstName = "Jane",
                lastName = "Doe",
                role = EmployeeRole.SECURITY,
                email = "jane.doe@test.com",
            )
        )
        coEvery { timeCardManager.getAllRecords() } returns Result.success(events)
        coEvery { employeeManager.getEmployeeList() } returns Result.success(employees)
        coEvery { stringProvider.getString(Res.string.title_timecard) } returns "Time Card"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_out) } returns "Clock Out"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_in) } returns "Clock In"

        // Act
        viewModel.loadEvents()

        // Assert
        coVerify { timeCardManager.getAllRecords() }
        coVerify { employeeManager.getEmployeeList() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test loadEvents handles failure`() = runCoroutineTest {
        // Arrange
        coEvery { timeCardManager.getAllRecords() } returns Result.failure(Exception("Error"))
        coEvery { employeeManager.getEmployeeList() } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_timecard) } returns "Time Card"

        // Act
        viewModel.loadEvents()

        // Assert
        coVerify { timeCardManager.getAllRecords() }
        coVerify { employeeManager.getEmployeeList() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test navigateToEmployee emits NavigateToScreen event`() = runCoroutineTest {
        // Arrange
        val employeeId = EmployeeId("123")

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        HomeDestination.TimeCardSingleEmployeeDestination(employeeId)
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToEmployee(employeeId)

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateToEmployeeList emits NavigateToScreen event`() = runCoroutineTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        HomeDestination.TimeCardEmployeeListDestination
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToEmployeeList()

        // Assert
        verificationJob.join()
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
}