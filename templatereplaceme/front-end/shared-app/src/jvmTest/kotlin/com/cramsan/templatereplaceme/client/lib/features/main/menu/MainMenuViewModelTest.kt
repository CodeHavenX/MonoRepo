package com.cramsan.templatereplaceme.client.lib.features.main.menu

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
import com.cramsan.templatereplaceme.client.lib.managers.UserManager
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Tests for [MainMenuViewModel].
 */
class MainMenuViewModelTest : CoroutineTest() {

    private lateinit var viewModel: MainMenuViewModel

    private lateinit var userManager: UserManager

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        userManager = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = MainMenuViewModel(
            dependencies = dependencies,
            userManager = userManager,
        )
    }

    @Test
    fun `initial ui state has empty fields`() = runCoroutineTest {
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("", viewModel.uiState.value.firstName)
        assertEquals("", viewModel.uiState.value.lastName)
    }

    @Test
    fun `changeFirstNameValue updates firstName in ui state`() = runCoroutineTest {
        viewModel.changeFirstNameValue("John")

        assertEquals("John", viewModel.uiState.value.firstName)
    }

    @Test
    fun `changeLastNameValue updates lastName in ui state`() = runCoroutineTest {
        viewModel.changeLastNameValue("Doe")

        assertEquals("Doe", viewModel.uiState.value.lastName)
    }
}
