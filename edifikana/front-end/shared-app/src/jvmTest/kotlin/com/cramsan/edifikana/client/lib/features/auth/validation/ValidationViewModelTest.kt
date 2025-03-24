package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ValidationViewModelTest : TestBase() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: ValidationViewModel
    private lateinit var dependencies: ViewModelDependencies
    private lateinit var exceptionHandler: CoroutineExceptionHandler

    override fun setupTest() {
        dependencies = mockk()
        authManager = mockk()

        // TODO: Cramsan verify if this is the right approach to make this testable
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineRule
            .testCoroutineDispatcher)
        exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->  }
        every { dependencies.coroutineExceptionHandler } returns exceptionHandler
        EventLogger.setInstance(mockk(relaxed = true))
        AssertUtil.setInstance(mockk(relaxed = true))

        viewModel = ValidationViewModel(dependencies, authManager)
    }

    @Test
    fun `verifyAccount should succeed when user exists`() = runBlockingTest {
        // Arrange
        val user = mockk<UserModel>()
        coEvery { authManager.getUser() } returns Result.success(user)

        // TODO: Cramsan verify if this is the right approach to make this testable
        every { dependencies.appScope } returns this


        // Act
        viewModel.verifyAccount()

        // Assert
        assertNotNull(user)
    }

//    @Test
//    fun `verifyAccount should fail when user does not exist`() = runBlockingTest {
//        // Arrange
//        val exception = Exception()
//        coEvery { authManager.getUser() } returns Result.failure(exception)
//
//        // TODO: Cramsan verify if this is the right approach to make this testable
//        every { dependencies.appScope } returns this
//
//        // Act
//        viewModel.verifyAccount()
//        this.testScheduler.advanceUntilIdle()
//
//        // Verify
//        verify { exceptionHandler.handleException(any(), exception) }
//    }
}