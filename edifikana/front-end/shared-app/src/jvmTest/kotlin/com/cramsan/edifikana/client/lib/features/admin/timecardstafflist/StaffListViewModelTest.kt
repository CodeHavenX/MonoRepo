package com.cramsan.edifikana.client.lib.features.admin.timecardstafflist

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.management.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.management.stafflist.StaffMemberUIModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StaffListViewModelTest : CoroutineTest() {

    private lateinit var viewModel: StaffListViewModel
    private lateinit var staffManager: StaffManager
    private lateinit var authManager: AuthManager
    private lateinit var organizationManager: OrganizationManager
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
        authManager = mockk(relaxed = true)
        organizationManager = mockk(relaxed = true)
        viewModel = StaffListViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            staffManager = staffManager,
            authManager = authManager,
            organizationManager = organizationManager,
        )
    }

    @Test
    fun `test loadStaffList updates UI state with staff list`() = runCoroutineTest {
        val staffList = listOf(
            StaffModel(
                id = StaffId("1"),
                firstName = "John",
                email = "john@example.com",
                idType = IdType.PASSPORT,
                lastName = "Doe",
                role = StaffRole.SECURITY_COVER,
            ),
            StaffModel(
                id = StaffId("2"),
                firstName = "Jane",
                email = "jane@example.com",
                idType = IdType.DNI,
                lastName = "Smith",
                role = StaffRole.MANAGER,
            )
        )
        coEvery { staffManager.getStaffList() } returns Result.success(staffList)

        viewModel.loadStaffList()

        assertEquals(
            listOf(
                StaffMemberUIModel(
                    staffId = StaffId("1"),
                    name = "John",
                    email = "john@example.com",
                ),
                StaffMemberUIModel(
                    staffId = StaffId("2"),
                    name = "Jane",
                    email = "jane@example.com",
                )
            ),
            viewModel.uiState.value.staffList
        )
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test loadStaffList with failure updates UI state with empty list`() = runCoroutineTest {
        coEvery { staffManager.getStaffList() } returns Result.failure(Exception("Error"))

        viewModel.loadStaffList()

        assertTrue(viewModel.uiState.value.staffList.isEmpty())
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test navigateToAddPrimaryStaff emits NavigateToScreen event`() = runCoroutineTest {
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
    fun `test navigateToAddSecondaryStaff emits NavigateToScreen event`() = runCoroutineTest {
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
    fun `test navigateToStaff emits NavigateToScreen event`() = runCoroutineTest {
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