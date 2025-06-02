package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
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
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [OtpValidationViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class ValidationViewModelTest : TestBase() {
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
     * Test that [OtpValidationViewModel.verifyAccount] succeeds when the user exists.
     */
    @Test
    fun `verifyAccount should succeed when user exists`() = runBlockingTest {
        // Arrange
        val user = mockk<UserModel>()
        coEvery { authManager.getUser() } returns Result.success(user)

        // Act
        viewModel.verifyAccount()

        // Assert
        assertTrue(exceptionHandler.exceptions.isEmpty())
        coVerify(exactly = 1) { authManager.getUser() }
    }

    /**
     * Test that [OtpValidationViewModel.verifyAccount] fails when the user does not exist.
     */
    @Test
    fun `verifyAccount should throw exception when user does not exist`() = runBlockingTest {
        // Arrange
        val exception = Exception()
        coEvery { authManager.getUser() } returns Result.failure(exception)

        // Act
        viewModel.verifyAccount()
        this.testScheduler.advanceUntilIdle()

        // Verify
        assertEquals(1, exceptionHandler.exceptions.size)
    }
}