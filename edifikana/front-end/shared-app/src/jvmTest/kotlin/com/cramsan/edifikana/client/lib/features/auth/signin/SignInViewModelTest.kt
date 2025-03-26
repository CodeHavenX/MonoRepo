package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.CollectorCoroutineExceptionHandler
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.applyNoopFrameworkSingletons
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach

/**
 * Test the [SignInViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class SignInViewModelTest : TestBase() {
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignInViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    /**
     * Setup the test.
     */
    @BeforeEach
    fun setupTest() {
        applyNoopFrameworkSingletons()
        authManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = SignInViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
            ),
            authManager)
    }

}