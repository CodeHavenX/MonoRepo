package com.cramsan.edifikana.client.lib.features.main.timecard

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
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
import edifikana_lib.time_card_event_clock_in
import edifikana_lib.time_card_event_clock_out
import edifikana_lib.title_timecard
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class TimeCartViewModelTest : TestBase() {

    private lateinit var timeCardManager: TimeCardManager
    private lateinit var staffManager: StaffManager
    private lateinit var viewModel: TimeCartViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        timeCardManager = mockk()
        staffManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        stringProvider = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = TimeCartViewModel(
            timeCardManager,
            staffManager,
            stringProvider = stringProvider,
            dependencies,
        )
    }

    @Test
    fun `test loadEvents successfully loads events`() = runBlockingTest {
        // Arrange
        val events = listOf(
            TimeCardRecordModel(
                id = TimeCardEventId("1"),
                staffPk = StaffId("1"),
                eventType = TimeCardEventType.CLOCK_IN,
                entityId = "entityId",
                propertyId = PropertyId("propertyId"),
                eventTime = 23524543,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
            ),
            TimeCardRecordModel(
                id = TimeCardEventId("2"),
                staffPk = StaffId("2"),
                eventType = TimeCardEventType.CLOCK_OUT,
                entityId = "entityId",
                propertyId = PropertyId("propertyId"),
                eventTime = 32523532126,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
            )
        )
        val staffs = listOf(
            StaffModel(
                id = StaffId("1"),
                idType = IdType.PASSPORT,
                name = "John",
                lastName = "Doe",
                role = StaffRole.SECURITY,
                email = "johndoe@test.com",
                status = StaffStatus.ACTIVE,
            ),
            StaffModel(
                id = StaffId("2"),
                idType = IdType.PASSPORT,
                name = "Jane",
                lastName = "Doe",
                role = StaffRole.SECURITY,
                email = "jane.doe@test.com",
                status = StaffStatus.ACTIVE,
            )
        )
        coEvery { timeCardManager.getAllRecords() } returns Result.success(events)
        coEvery { staffManager.getStaffList() } returns Result.success(staffs)
        coEvery { stringProvider.getString(Res.string.title_timecard) } returns "Time Card"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_out) } returns "Clock Out"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_in) } returns "Clock In"

        // Act
        viewModel.loadEvents()

        // Assert
        coVerify { timeCardManager.getAllRecords() }
        coVerify { staffManager.getStaffList() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test loadEvents handles failure`() = runBlockingTest {
        // Arrange
        coEvery { timeCardManager.getAllRecords() } returns Result.failure(Exception("Error"))
        coEvery { staffManager.getStaffList() } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_timecard) } returns "Time Card"

        // Act
        viewModel.loadEvents()

        // Assert
        coVerify { timeCardManager.getAllRecords() }
        coVerify { staffManager.getStaffList() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test navigateToStaff emits NavigateToScreen event`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("123")

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToScreen(
                        ManagementDestination.TimeCardSingleStaffDestination(staffId)
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToStaff(staffId)

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateToStaffList emits NavigateToScreen event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToScreen(
                        ManagementDestination.TimeCardStaffListDestination
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToStaffList()

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