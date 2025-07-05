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
            authManager)
    }

    /**
     * Test that the ViewModel initializes with the correct initial state.
     */
    @Test
    fun `setEmailAddress should update email in UI state and call sendOtpCode`() = runCoroutineTest {
        // Arrange
        val email = "test@example.com"
        coEvery { authManager.sendOtpCode(email) } returns Unit

        // Act
        viewModel.initializeOTPValidationScreen(email)
        this.testScheduler.advanceUntilIdle()

        // Assert
        Assertions.assertEquals(email, viewModel.uiState.value.email)
        coVerify { authManager.sendOtpCode(email) }

    }

    /**
     * Test that signInWithOtp calls authManager with the correct parameters.
     */
    @Test
    fun `signInWithOtp should call signInWithOtp on authManager with correct params`() = runCoroutineTest {
        // Arrange
        val email = "user@domain.com"
        val otp = listOf(1,2,3,4,5,6)
        viewModel.initializeOTPValidationScreen(email)
        this.testScheduler.advanceUntilIdle()
        otp.forEachIndexed { index, value ->
            viewModel.onEnterOtpValue(value, index)
        }
        coEvery { authManager.signInWithOtp(email, otp.toString()) } returns Result.success(mockk())

        // Act
        viewModel.signInWithOtp()
        this.testScheduler.advanceUntilIdle()

        // Assert
        coVerify { authManager.signInWithOtp(email, otp.toString()) }
    }

    /**
     * Test that onOtpFieldFocused updates the focusedIndex in the UI state.
     */
    @Test
    fun `onOtpFieldFocused should update focusedIndex`() = runCoroutineTest {
        // Act
        viewModel.onOtpFieldFocused(3)
        this.testScheduler.advanceUntilIdle()

        // Assert
        Assertions.assertEquals(3, viewModel.uiState.value.focusedIndex)
    }

    /**
     * Test that onEnterOtpValue updates the otpCode and focusedIndex in the UI state.
     */
    @Test
    fun `onEnterOtpValue should update otpCode and focusedIndex`() = runCoroutineTest {
        // Act
        viewModel.onOtpFieldFocused(0)
        this.testScheduler.advanceUntilIdle()
        viewModel.onEnterOtpValue(5, 0)
        this.testScheduler.advanceUntilIdle()

        // Assert
        Assertions.assertEquals(5, viewModel.uiState.value.otpCode[0])
        Assertions.assertEquals(1, viewModel.uiState.value.focusedIndex)
    }

    /**
     * Test that onKeyboardBack clears the previous otpCode and updates focusedIndex.
     */
    @Test
    fun `onKeyboardBack should clear previous otpCode and update focusedIndex`() = runCoroutineTest {
        // Act
        viewModel.onOtpFieldFocused(2)
        this.testScheduler.advanceUntilIdle()
        viewModel.onEnterOtpValue(7, 2)
        this.testScheduler.advanceUntilIdle()
        viewModel.onKeyboardBack()
        this.testScheduler.advanceUntilIdle()

        // Assert
        Assertions.assertEquals(null, viewModel.uiState.value.otpCode[3])
        Assertions.assertEquals(2, viewModel.uiState.value.focusedIndex)
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