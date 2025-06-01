package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.main.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.StaffStatus
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
import edifikana_lib.Res
import edifikana_lib.title_timecard_staff_list
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StaffListViewModelTest : TestBase() {

    private lateinit var staffManager: StaffManager
    private lateinit var viewModel: StaffListViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        staffManager = mockk()
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
        viewModel = StaffListViewModel(
            staffManager,
            stringProvider = stringProvider,
            dependencies,
        )
    }

    @Test
    fun `test loadStaffs successfully loads staff members`() = runBlockingTest {
        // Arrange
        val staffList = listOf(
            StaffModel(
                id = StaffId("1"),
                idType = IdType.OTHER,
                name = "Staff 1",
                lastName = "Last 1",
                role = StaffRole.SECURITY,
                email = "staff1@test.com",
                status = StaffStatus.ACTIVE,
            )
        )
        coEvery { staffManager.getStaffList() } returns Result.success(staffList)
        coEvery { stringProvider.getString(Res.string.title_timecard_staff_list) } returns "Staff List"

        // Act
        viewModel.loadStaffs()

        // Assert
        coVerify { staffManager.getStaffList() }
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.staffs.size)
        assertEquals("Staff 1 Last 1", uiState.staffs[0].fullName)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test loadStaffs handles failure`() = runBlockingTest {
        // Arrange
        coEvery { staffManager.getStaffList() } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_timecard_staff_list) } returns "Staff List"

        // Act
        viewModel.loadStaffs()

        // Assert
        coVerify { staffManager.getStaffList() }
        val uiState = viewModel.uiState.value
        assertEquals(0, uiState.staffs.size)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test navigateToStaff emits NavigateToScreen event`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("123")

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
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