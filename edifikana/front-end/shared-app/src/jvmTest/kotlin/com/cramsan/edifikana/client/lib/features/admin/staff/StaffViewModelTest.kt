package com.cramsan.edifikana.client.lib.features.admin.staff

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
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
class StaffViewModelTest : TestBase() {

    private lateinit var viewModel: StaffViewModel
    private lateinit var staffManager: StaffManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        staffManager = mockk(relaxed = true)
        viewModel = StaffViewModel(
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
    fun `test loadStaff with valid staffId updates UI state`() = runBlockingTest {
        val staffId = StaffId("123")
        val staff = StaffModel(
            id = staffId,
            idType = IdType.DNI,
            name = "John",
            lastName = "Doe",
            role = StaffRole.SECURITY_COVER,
            email = "test@test.com",
            status = StaffStatus.PENDING,
        )
        coEvery { staffManager.getStaff(staffId) } returns Result.success(staff)

        viewModel.loadStaff(staffId)

        assertEquals("John Doe", viewModel.uiState.value.title)
        assertEquals(IdType.DNI, viewModel.uiState.value.idType)
        assertEquals("John", viewModel.uiState.value.firstName)
        assertEquals("Doe", viewModel.uiState.value.lastName)
        assertEquals(StaffRole.SECURITY_COVER, viewModel.uiState.value.role)
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test loadStaff with invalid staffId updates UI state with empty title`() = runBlockingTest {
        val staffId = StaffId("123")
        coEvery { staffManager.getStaff(staffId) } returns Result.failure(Exception("Error"))

        viewModel.loadStaff(staffId)

        assertEquals("", viewModel.uiState.value.title)
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test onBackSelected emits NavigateBack event`() = runBlockingTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }
        viewModel.onBackSelected()
        verificationJob.join()
    }
}