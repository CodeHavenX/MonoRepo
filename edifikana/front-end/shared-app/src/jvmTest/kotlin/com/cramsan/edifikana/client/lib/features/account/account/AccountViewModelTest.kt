package com.cramsan.edifikana.client.lib.features.account.account

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.UserId
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
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var viewModel: AccountViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            windowEventReceiver = windowEventBus,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = AccountViewModel(authManager, dependencies)
    }

    @Test
    fun `test signOut emits NavigateToActivity event`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.signOut() } returns Result.success(Unit)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToActivity(
                        ActivityRouteDestination.AuthRouteDestination,
                        clearStack = true,
                    ),
                    awaitItem(),
                )
            }
        }

        viewModel.signOut()

        // Assert
        coVerify { authManager.signOut() }
        verificationJob.join()
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        // Arrange

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
        verificationJob.join()    }

    @Test
    fun `test loadUserData updates UI state with user data`() = runCoroutineTest {
        // Arrange
        val user = UserModel(
            id = UserId("123"),
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            authMetadata = null,
        )
        coEvery { authManager.getUser() } returns Result.success(user)

        // Act
        viewModel.loadUserData()

        // Assert
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("John", uiState.firstName)
        assertEquals("Doe", uiState.lastName)
        assertEquals("john.doe@example.com", uiState.email)
        assertEquals("1234567890", uiState.phoneNumber)
    }

    @Test
    fun `test loadUserData handles failure`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.getUser() } returns Result.failure(Exception("Error"))

        // Act
        viewModel.loadUserData()

        // Assert
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("", uiState.firstName)
        assertEquals("", uiState.lastName)
        assertEquals("", uiState.email)
        assertEquals("", uiState.phoneNumber)
    }
}