package com.cramsan.edifikana.client.lib.features.account.account

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach

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
    fun `test signOut emits NavigateToNavGraph event`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.signOut() } returns Result.success(Unit)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToNavGraph(
                        EdifikanaNavGraphDestination.AuthNavGraphDestination,
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
    fun `test navigateBack in view mode emits NavigateBack event`() = runCoroutineTest {
        // Arrange
        assertEquals(false, viewModel.uiState.value.isEditable)

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

    @Test
    fun `test navigateBack in edit mode cancels edit and stays on screen`() = runCoroutineTest {
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

        // Enter edit mode first
        viewModel.editOrSave()
        assertEquals(true, viewModel.uiState.value.isEditable)

        // Act
        viewModel.navigateBack()

        // Assert - should NOT emit NavigateBack event, should call cancelEdit instead
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isEditable)
        assertEquals("John", uiState.firstName)
    }

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

    @Test
    fun `test cancelEdit reloads user data and exits edit mode`() = runCoroutineTest {
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

        // First enter edit mode
        viewModel.editOrSave()
        assertEquals(true, viewModel.uiState.value.isEditable)

        // Act
        viewModel.cancelEdit()

        // Assert
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(false, uiState.isEditable)
        assertEquals("John", uiState.firstName)
        assertEquals("Doe", uiState.lastName)
        assertEquals("john.doe@example.com", uiState.email)
        assertEquals("1234567890", uiState.phoneNumber)
    }

    @Test
    fun `test cancelEdit handles failure`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.getUser() } returns Result.failure(Exception("Error"))

        // First enter edit mode
        viewModel.editOrSave()
        assertEquals(true, viewModel.uiState.value.isEditable)

        // Act
        viewModel.cancelEdit()

        // Assert
        coVerify { authManager.getUser() }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(false, uiState.isEditable)
    }

    @Test
    fun `test editOrSave enters edit mode when not editable`() = runCoroutineTest {
        // Arrange
        assertEquals(false, viewModel.uiState.value.isEditable)

        // Act
        viewModel.editOrSave()

        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(true, uiState.isEditable)
    }

    @Test
    fun `test editOrSave saves changes when already editable`() = runCoroutineTest {
        // Arrange
        coEvery {
            authManager.updateUser(
                firstName = "Jane",
                lastName = "Smith",
                email = "jane.smith@example.com",
                phoneNumber = "9876543210",
            )
        } returns Result.success(Unit)

        // Enter edit mode and update fields
        viewModel.editOrSave()
        viewModel.updateFirstName("Jane")
        viewModel.updateLastName("Smith")
        viewModel.updateEmail("jane.smith@example.com")
        viewModel.updatePhoneNumber("9876543210")
        assertEquals(true, viewModel.uiState.value.isEditable)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Account information updated successfully."),
                    awaitItem(),
                )
            }
        }

        viewModel.editOrSave()

        // Assert
        coVerify {
            authManager.updateUser(
                firstName = "Jane",
                lastName = "Smith",
                email = "jane.smith@example.com",
                phoneNumber = "9876543210",
            )
        }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(false, uiState.isEditable)
        verificationJob.join()
    }

    @Test
    fun `test editOrSave handles save failure`() = runCoroutineTest {
        // Arrange
        coEvery {
            authManager.updateUser(
                firstName = any(),
                lastName = any(),
                email = any(),
                phoneNumber = any(),
            )
        } returns Result.failure(Exception("Network error"))

        // Enter edit mode
        viewModel.editOrSave()
        assertEquals(true, viewModel.uiState.value.isEditable)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to update account information. Please try again."),
                    awaitItem(),
                )
            }
        }

        viewModel.editOrSave()

        // Assert
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals(true, uiState.isEditable) // Should stay in edit mode on failure
        verificationJob.join()
    }
}