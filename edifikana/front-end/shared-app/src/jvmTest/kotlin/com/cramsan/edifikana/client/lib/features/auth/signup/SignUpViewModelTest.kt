package com.cramsan.edifikana.client.lib.features.auth.signup

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test the [SignUpViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class SignUpViewModelTest : TestBase() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignUpViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var stringProvider: StringProvider


    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        viewModel = SignUpViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            auth = authManager,
            stringProvider = stringProvider,
        )
    }

    /**
     * Test the [SignUpViewModel.initializePage] method.
     */
    @Test
    fun `test initializePage has expected UI state`() = runBlockingTest {
        // Act
        viewModel.initializePage()

        // Assert
        assertEquals(SignUpUIState.Initial, viewModel.uiState.value)
    }

    /**
     * Test the [SignUpViewModel.onEmailValueChange] method.
     */
    @Test
    fun `test onEmailValueChange updates email value`() = runBlockingTest {
        // Arrange
        val email = "test@example.com"

        // Act
        viewModel.onEmailValueChange(email)

        // Assert
        assertEquals(email, viewModel.uiState.value.email)
    }

    /**
     * Test the [SignUpViewModel.onPhoneNumberValueChange] method.
     */
    @Test
    fun `test onPhoneNumberValueChange updates phone number value`() = runBlockingTest {
        // Arrange
        val phoneNumber = "1234567890"

        // Act
        viewModel.onPhoneNumberValueChange(phoneNumber)

        // Assert
        assertEquals(phoneNumber, viewModel.uiState.value.phoneNumber)
    }

    /**
     * Test the [SignUpViewModel.onFirstNameValueChange] method.
     */
    @Test
    fun `test onFirstNameValueChange updates first name value`() = runBlockingTest {
        // Arrange
        val firstName = "John"

        // Act
        viewModel.onFirstNameValueChange(firstName)

        // Assert
        assertEquals(firstName, viewModel.uiState.value.firstName)
    }

    /**
     * Test the [SignUpViewModel.onLastNameValueChange] method.
     */
    @Test
    fun `test onLastNameValueChange updates last name value`() = runBlockingTest {
        // Arrange
        val lastName = "Doe"

        // Act
        viewModel.onLastNameValueChange(lastName)

        // Assert
        assertEquals(lastName, viewModel.uiState.value.lastName)
    }

    /**
     * Test the [SignUpViewModel.onPolicyChecked] method.
     */
    @Test
    fun `test onPolicyChecked updates policy checked value true`() = runBlockingTest {
        // Arrange
        val isChecked = true

        // Act
        viewModel.onPolicyChecked(isChecked)

        // Assert
        assertTrue(viewModel.uiState.value.policyChecked)
        assertTrue(viewModel.uiState.value.registerEnabled)
    }

    /**
     * Test the [SignUpViewModel.onPolicyChecked] method with false value.
     */
    @Test
    fun `test onPolicyChecked updates policy checked value to false`() = runBlockingTest {
        // Arrange
        val isChecked = false

        // Act
        viewModel.onPolicyChecked(isChecked)

        // Assert
        assertFalse(viewModel.uiState.value.policyChecked)
        assertFalse(viewModel.uiState.value.registerEnabled)
    }

    /**
     * Test navigate back calls event
     */
    @Test
    fun `test navigateBack calls emitEvent`() = runBlockingTest {
        // Arrange
        coEvery { authManager.signUp(any(), any(), any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack, awaitItem()
                )
            }
        }
        viewModel.navigateBack()

        // Verify
        verificationJob.join()
    }

    /**
     * Test the [SignUpViewModel.signUp] method with valid inputs
     */
    @Test
    fun `test signUp redirects to otp validation screen`() = runBlockingTest {
        // Arrange
        val firstName = "Dwayne"
        val lastName = "Johnson"
        val email = "totalReal@email.com"
        val phoneNumber = "4456879214"

        coEvery {
            authManager.signUp(
                email, phoneNumber, firstName, lastName
            )
        } returns Result.success(mockk())

        viewModel.onFirstNameValueChange(firstName)
        viewModel.onLastNameValueChange(lastName)
        viewModel.onEmailValueChange(email)
        viewModel.onPhoneNumberValueChange(phoneNumber)
        viewModel.onPolicyChecked(true)

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AuthRouteDestination.ValidationDestination(email)), awaitItem()
                )
            }
        }
        viewModel.signUp()


        // Verify
        coVerify { authManager.signUp(email, phoneNumber, firstName, lastName) }
        verificationJob.join()
        assertTrue(viewModel.uiState.value.isLoading)
    }

    /**
     * Test the [SignUpViewModel.signUp] method with various invalid inputs returns validation errors
     */
    @ParameterizedTest
    @CsvFileSource(resources = ["/sign_up_test_validation_errors.csv"], numLinesToSkip = 1)
    fun `test signUp with validation errors`(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        expectedErrorCount: Int,
    ) = runBlockingTest {
        // Arrange
        viewModel.onFirstNameValueChange(firstName)
        viewModel.onLastNameValueChange(lastName)
        viewModel.onEmailValueChange(email)
        viewModel.onPhoneNumberValueChange(phoneNumber)

        // Act
        viewModel.signUp()

        // Assert
        assertEquals(expectedErrorCount, viewModel.uiState.value.errorMessage?.size ?: 0)
    }
}