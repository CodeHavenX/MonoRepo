package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class AddPrimaryStaffViewModelTest : TestBase() {

    private lateinit var viewModel: AddPrimaryStaffViewModel
    private lateinit var staffManager: StaffManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = SharedFlowApplicationReceiver()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        staffManager = mockk(relaxed = true)
        viewModel = AddPrimaryStaffViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
            ),
            staffManager = staffManager
        )
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.navigateBack()
        verificationJob.join()
    }

    @Test
    fun `test invite with invalid email updates UI state with error`() = runBlockingTest {
        val invalidEmail = "invalid-email"

        viewModel.invite(invalidEmail)

        assertTrue(exceptionHandler.exceptions.isEmpty())
        assertEquals(
            "Invalid email format.",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun `test invite with valid email sends invite and navigates back`() = runBlockingTest {
        val validEmail = "test@example.com"
        coEvery { staffManager.inviteStaff(validEmail) } returns Result.success(Unit)

        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.ShowSnackbar(
                        "Email was sent to $validEmail to join this organization."
                    ),
                    awaitItem()
                )
                assertEquals(EdifikanaApplicationEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.invite(validEmail)
        verificationJob.join()

        coVerify { staffManager.inviteStaff(validEmail) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test invite with valid email but failure response updates UI state with error`() = runBlockingTest {
        val validEmail = "test@example.com"
        coEvery { staffManager.inviteStaff(validEmail) } returns Result.failure(Exception("Error"))

        viewModel.invite(validEmail)

        assertTrue(exceptionHandler.exceptions.isEmpty())
        assertEquals(
            "There was an error processing the request.",
            viewModel.uiState.value.errorMessage
        )
    }
}