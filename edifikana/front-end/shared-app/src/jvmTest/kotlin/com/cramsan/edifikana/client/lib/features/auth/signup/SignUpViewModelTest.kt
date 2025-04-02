package com.cramsan.edifikana.client.lib.features.auth.signup

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.CollectorCoroutineExceptionHandler
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
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

/**
 * Test the [SignUpViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class SignUpViewModelTest : TestBase() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignUpViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver


    /**
     * Setup the test.
     */
    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        viewModel = SignUpViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
            ), authManager
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
        assertEquals(email, viewModel.uiState.value.signUpForm.email)
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
        assertEquals(phoneNumber, viewModel.uiState.value.signUpForm.phoneNumber)
    }

    /**
     * Test the [SignUpViewModel.onPasswordValueChange] method.
     */
    @Test
    fun `test onPasswordValueChange updates password value`() = runBlockingTest {
        // Arrange
        val password = "p@ssWord123"

        // Act
        viewModel.onPasswordValueChange(password)

        // Assert
        assertEquals(password, viewModel.uiState.value.signUpForm.password)
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
        assertEquals(firstName, viewModel.uiState.value.signUpForm.firstName)
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
        assertEquals(lastName, viewModel.uiState.value.signUpForm.lastName)
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
        assertTrue(viewModel.uiState.value.signUpForm.policyChecked)
        assertTrue(viewModel.uiState.value.signUpForm.registerEnabled)
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
        assertFalse(viewModel.uiState.value.signUpForm.policyChecked)
        assertFalse(viewModel.uiState.value.signUpForm.registerEnabled)
    }

    /**
     * Test navigate back calls event
     */
    @Test
    fun `test navigateBack calls emitEvent`() = runBlockingTest {
        // Arrange
        coEvery { authManager.signUp(any(), any(), any(), any(), any()) } returns Result.success(mockk())

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateBack, awaitItem()
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
    fun `test signUp success`() = runBlockingTest {
        // Arrange
        val firstName = "Dwayne"
        val lastName = "Johnson"
        val email = "totalReal@email.com"
        val phoneNumber = "4456879214"
        val password = "p@ssWord123"

        coEvery {
            authManager.signUp(
                email, phoneNumber, password, firstName, lastName
            )
        } returns Result.success(mockk())
        coEvery { authManager.signIn(email, password) } returns Result.success(mockk())

        viewModel.onFirstNameValueChange(firstName)
        viewModel.onLastNameValueChange(lastName)
        viewModel.onEmailValueChange(email)
        viewModel.onPhoneNumberValueChange(phoneNumber)
        viewModel.onPasswordValueChange(password)
        viewModel.onPolicyChecked(true)

        // Act
        viewModel.signUp()

        // Verify
        coVerify { authManager.signUp(email, phoneNumber, password, firstName, lastName) }
        coVerify { authManager.signIn(email, password) }
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
        password: String,
        expectedErrorCount: Int,
    ) = runBlockingTest {
        // Arrange
        viewModel.onFirstNameValueChange(firstName)
        viewModel.onLastNameValueChange(lastName)
        viewModel.onEmailValueChange(email)
        viewModel.onPhoneNumberValueChange(phoneNumber)
        viewModel.onPasswordValueChange(password)

        // Act
        viewModel.signUp()

        // Assert
        assertEquals(expectedErrorCount, viewModel.uiState.value.signUpForm.errorMessage?.size ?: 0)
    }

    /**
     * Test the [SignUpViewModel.signUp] method with valid inputs but fails to sign up
     */
    @Test
    fun `test signUp fails with valid inputs`() = runBlockingTest {
        val firstName = "Diana"
        val lastName = "Prince"
        val email = "valid@email.com"
        val phoneNumber = "5051113234"
        val password = "p@ssWord123"
        val expectedErrorMessage = listOf("There was an unexpected error.")

        coEvery { authManager.signUp(email, phoneNumber, password, firstName, lastName) } returns Result.failure(
            Exception("Sign in failed")
        )

        viewModel.onFirstNameValueChange(firstName)
        viewModel.onLastNameValueChange(lastName)
        viewModel.onEmailValueChange(email)
        viewModel.onPhoneNumberValueChange(phoneNumber)
        viewModel.onPasswordValueChange(password)
        viewModel.onPolicyChecked(true)

        // Act
        viewModel.signUp()

        // Assert & verify
        coVerify { authManager.signUp(email, phoneNumber, password, firstName, lastName) }
        assertEquals(expectedErrorMessage.toString(), viewModel.uiState.value.signUpForm.errorMessage.toString())
    }

    /**
     * Test the [SignUpViewModel.signUp] method with valid inputs but fails to sign in
     * TODO: Fix logging capabilities to be able to figure out why this test fails on CI but no on local
     */
    @Ignore
    @Test
    fun `test signUp fails with valid inputs when trying to signIn`() = runBlockingTest {
        val firstName = "Diana"
        val lastName = "Prince"
        val email = "valid@email.com"
        val phoneNumber = "5051113234"
        val password = "p@ssWord123"
        val expectedErrorMessage = listOf("There was an unexpected error.")

        coEvery {
            authManager.signUp(
                email, phoneNumber, password, firstName, lastName
            )
        } returns Result.success(mockk())
        coEvery { authManager.signIn(email, password) } returns Result.failure(Exception("Sign in failed"))

        viewModel.onFirstNameValueChange(firstName)
        viewModel.onLastNameValueChange(lastName)
        viewModel.onEmailValueChange(email)
        viewModel.onPhoneNumberValueChange(phoneNumber)
        viewModel.onPasswordValueChange(password)
        viewModel.onPolicyChecked(true)

        // Act
        viewModel.signUp()

        // Assert & verify
        coVerify { authManager.signUp(email, phoneNumber, password, firstName, lastName) }
        assertEquals(
            expectedErrorMessage.toString(), viewModel.uiState.value.signUpForm.errorMessage.toString()
        )

    }
}