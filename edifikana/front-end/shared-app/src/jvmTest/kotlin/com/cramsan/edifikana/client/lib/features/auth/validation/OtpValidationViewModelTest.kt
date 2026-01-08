package com.cramsan.edifikana.client.lib.features.auth.validation

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
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
import org.junit.jupiter.api.Assertions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [OtpValidationViewModel] class.
 */
@Suppress("UNCHECKED_CAST")
class OtpValidationViewModelTest : CoroutineTest() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: OtpValidationViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        windowEventBus = EventBus()
        applicationEventReceiver = EventBus()
        viewModel = OtpValidationViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            auth = authManager,
            organizationManager = mockk(),
            stringProvider = mockk(),
        )
    }

    /**
     * Test that the ViewModel initializes with the correct initial state.
     */
    @Test
    fun `setEmailAddress should update email in UI state`() = runCoroutineTest {
        // Arrange
        val email = "test@example.com"
        coEvery { authManager.sendOtpCode(email) } returns Result.success(Unit)

        // Act
        viewModel.initializeOTPValidationScreen(email, accountCreationFlow = false)
        this.testScheduler.advanceUntilIdle()

        // Assert
        Assertions.assertEquals(email, viewModel.uiState.value.email)
        Assertions.assertFalse(viewModel.uiState.value.accountCreationFlow)
    }

    /**
     * Test that signInWithOtp does not calls authManager when incorrect parameters.
     */
    @Test
    fun `signInWithOtp does not call signInWithOtp due to incorrect params`() = runCoroutineTest {
        // Arrange
        val email = "user@domain.com"
        viewModel.initializeOTPValidationScreen(email, accountCreationFlow = false)
        this.testScheduler.advanceUntilIdle()
        viewModel.updateOtpCode("123")

        // Act
        viewModel.signInWithOtp()
        this.testScheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 0) { authManager.signInWithOtp(any(), any(), any()) }
    }

    /**
     * Test that signInWithOtp calls authManager with the correct parameters.
     */
    @Test
    fun `signInWithOtp should call signInWithOtp on authManager with correct params`() = runCoroutineTest {
        // Arrange
        val email = "user@domain.com"
        val otpString = "123456"
        viewModel.initializeOTPValidationScreen(email, accountCreationFlow = false)
        this.testScheduler.advanceUntilIdle()
        viewModel.updateOtpCode(otpString)

        coEvery { authManager.signInWithOtp(
            email,
            otpString,
            createUser = false,
        ) } returns Result.success(mockk())

        // Act
        viewModel.signInWithOtp()
        this.testScheduler.advanceUntilIdle()

        // Assert
        coVerify { authManager.signInWithOtp(
            email,
            otpString,
            createUser = false,
        ) }
    }

    /**
     * Test that navigateBack emits a NavigateBack event on the window event bus.
     */
    @Test
    fun `navigateBack should call emitEvent`() = runCoroutineTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack, awaitItem()
                )
            }
        }
        viewModel.navigateBack()
        this.testScheduler.advanceUntilIdle()

        // Assert
        verificationJob.join()
    }
}