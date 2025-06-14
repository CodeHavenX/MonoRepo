package com.cramsan.edifikana.client.lib.features.admin.timecardstafflist

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.management.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.management.stafflist.StaffUIModel
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
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StaffListViewModelTest : TestBase() {

    private lateinit var viewModel: StaffListViewModel
    private lateinit var staffManager: StaffManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        staffManager = mockk(relaxed = true)
        windowEventBus = EventBus()
        viewModel = StaffListViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            staffManager = staffManager
        )
    }

    @Test
    fun `test loadStaffList updates UI state with staff list`() = runBlockingTest {
        val staffList = listOf(
            StaffModel(
                id = StaffId("1"),
                name = "John",
                email = "john@example.com",
                status = StaffStatus.PENDING,
                idType = IdType.PASSPORT,
                lastName = "Doe",
                role = StaffRole.SECURITY_COVER,
            ),
            StaffModel(
                id = StaffId("2"),
                name = "Jane",
                email = "jane@example.com",
                status = StaffStatus.ACTIVE,
                idType = IdType.DNI,
                lastName = "Smith",
                role = StaffRole.ADMIN,
            )
        )
        coEvery { staffManager.getStaffList() } returns Result.success(staffList)

        viewModel.loadStaffList()

        assertEquals(
            listOf(
                StaffUIModel(
                    id = StaffId("1"),
                    name = "John",
                    email = "john@example.com",
                    status = StaffStatus.PENDING,
                ),
                StaffUIModel(
                    id = StaffId("2"),
                    name = "Jane",
                    email = "jane@example.com",
                    status = StaffStatus.ACTIVE,
                )
            ),
            viewModel.uiState.value.staffList
        )
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test loadStaffList with failure updates UI state with empty list`() = runBlockingTest {
        coEvery { staffManager.getStaffList() } returns Result.failure(Exception("Error"))

        viewModel.loadStaffList()

        assertTrue(viewModel.uiState.value.staffList.isEmpty())
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test navigateToAddPrimaryStaff emits NavigateToScreen event`() = runBlockingTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.AddPrimaryStaffManagementDestination),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddPrimaryStaff()
        verificationJob.join()
    }

    @Test
    fun `test navigateToAddSecondaryStaff emits NavigateToScreen event`() = runBlockingTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.AddSecondaryStaffManagementDestination),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddSecondaryStaff()
        verificationJob.join()
    }

    @Test
    fun `test navigateToStaff emits NavigateToScreen event`() = runBlockingTest {
        val staffId = StaffId("123")
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.StaffDestination(staffId)),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToStaff(staffId)
        verificationJob.join()
    }
}