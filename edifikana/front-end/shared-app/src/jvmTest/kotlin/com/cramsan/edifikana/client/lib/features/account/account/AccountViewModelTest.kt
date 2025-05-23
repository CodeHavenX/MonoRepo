package com.cramsan.edifikana.client.lib.features.account.account

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.UserId
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach


@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest : TestBase() {

    private lateinit var authManager: AuthManager
    private lateinit var viewModel: AccountViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = AccountViewModel(authManager, dependencies)
    }

    @Test
    fun `test signOut emits NavigateToActivity event`() = runBlockingTest {
        // Arrange
        coEvery { authManager.signOut() } returns Result.success(Unit)

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToActivity(
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
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        // Arrange

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
        verificationJob.join()    }

    @Test
    fun `test loadUserData updates UI state with user data`() = runBlockingTest {
        // Arrange
        val user = UserModel(
            id = UserId("123"),
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            hasGlobalPerms = false,
            isVerified = true,
        )
        coEvery { authManager.getUser() } returns Result.success(user)

        // Act
        viewModel.loadUserData()

        // Assert
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("John", uiState.content.firstName)
        assertEquals("Doe", uiState.content.lastName)
        assertEquals("john.doe@example.com", uiState.content.email)
        assertEquals("1234567890", uiState.content.phoneNumber)
    }

    @Test
    fun `test loadUserData handles failure`() = runBlockingTest {
        // Arrange
        coEvery { authManager.getUser() } returns Result.failure(Exception("Error"))

        // Act
        viewModel.loadUserData()

        // Assert
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("", uiState.content.firstName)
        assertEquals("", uiState.content.lastName)
        assertEquals("", uiState.content.email)
        assertEquals("", uiState.content.phoneNumber)
    }
}