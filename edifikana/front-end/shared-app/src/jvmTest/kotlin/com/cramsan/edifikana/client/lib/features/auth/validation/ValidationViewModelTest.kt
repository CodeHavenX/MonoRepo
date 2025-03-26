package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.framework.core.CollectorCoroutineExceptionHandler
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.applyNoopFrameworkSingletons
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test the [ValidationViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class ValidationViewModelTest : TestBase() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: ValidationViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    /**
     * Setup the test.
     */
    @BeforeEach
    fun setupTest() {
        applyNoopFrameworkSingletons()
        authManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = ValidationViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
            ),
            authManager)
    }

    /**
     * Test that [ValidationViewModel.verifyAccount] succeeds when the user exists.
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
     * Test that [ValidationViewModel.verifyAccount] fails when the user does not exist.
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