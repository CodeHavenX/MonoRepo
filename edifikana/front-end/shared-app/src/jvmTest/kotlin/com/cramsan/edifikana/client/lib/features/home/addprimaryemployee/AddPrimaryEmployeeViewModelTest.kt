package com.cramsan.edifikana.client.lib.features.home.addprimaryemployee

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.addprimaryemployee.AddPrimaryEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
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
import edifikana_lib.text_there_was_an_error_processing_request
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddPrimaryEmployeeViewModelTest : CoroutineTest() {

    private lateinit var viewModel: AddPrimaryEmployeeViewModel
    private lateinit var employeeManager: EmployeeManager
    private lateinit var authManager: AuthManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var stringProvider: StringProvider
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        employeeManager = mockk(relaxed = true)
        authManager = mockk(relaxed = true)
        stringProvider = mockk()
        viewModel = AddPrimaryEmployeeViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            stringProvider = stringProvider,
            authManager = authManager,
        )
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.navigateBack()
        verificationJob.join()
    }

    @Test
    fun `test invite with invalid email updates UI state with error`() = runCoroutineTest {
        val invalidEmail = "invalid-email"

        viewModel.invite(invalidEmail)

        assertTrue(exceptionHandler.exceptions.isEmpty())
        assertEquals(
            "Invalid email format.",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun `test invite with valid email sends invite and navigates back`() = runCoroutineTest {
        val validEmail = "test@example.com"
        coEvery { authManager.inviteEmployee(validEmail) } returns Result.success(Unit)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Email was sent to $validEmail to join this organization."
                    ),
                    awaitItem()
                )
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.invite(validEmail)
        verificationJob.join()

        coVerify { authManager.inviteEmployee(validEmail) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test invite with valid email but failure response updates UI state with error`() = runCoroutineTest {
        val validEmail = "test@example.com"
        coEvery { authManager.inviteEmployee(validEmail) } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.text_there_was_an_error_processing_request) } returns "There was an error processing the request."

        viewModel.invite(validEmail)

        assertTrue(exceptionHandler.exceptions.isEmpty())
        assertEquals(
            "There was an error processing the request.",
            viewModel.uiState.value.errorMessage
        )
    }
}