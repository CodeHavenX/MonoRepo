package com.cramsan.templatereplaceme.client.lib.features.main.menu

import app.cash.turbine.turbineScope
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
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import com.cramsan.templatereplaceme.client.lib.managers.PingPongManager
import com.cramsan.templatereplaceme.client.lib.models.PongModel
import com.cramsan.templatereplaceme.lib.model.PingPong
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [MainMenuViewModel].
 */
class MainMenuViewModelTest : CoroutineTest() {

    private lateinit var viewModel: MainMenuViewModel

    private lateinit var pingPongManager: PingPongManager

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        pingPongManager = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = MainMenuViewModel(
            dependencies = dependencies,
            pingPongManager = pingPongManager,
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

    @Test
    fun `ping on success emits ShowSnackbar with pong message`() = runCoroutineTest {
        val pong = PongModel(id = PingPong("id-1"), firstName = "John", lastName = "Doe")
        viewModel.changeFirstNameValue("John")
        viewModel.changeLastNameValue("Doe")
        coEvery { pingPongManager.ping("John", "Doe") } returns Result.success(pong)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.ping()

            val event = turbine.awaitItem()
            assertTrue(event is TemplateReplaceMeWindowsEvent.ShowSnackbar)
            assertTrue((event as TemplateReplaceMeWindowsEvent.ShowSnackbar).message.contains("John"))
            assertFalse(viewModel.uiState.value.isLoading)
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        coVerify { pingPongManager.ping("John", "Doe") }
    }

    @Test
    fun `ping on failure emits ShowSnackbar with error message`() = runCoroutineTest {
        val exception = Exception("Service unavailable")
        coEvery { pingPongManager.ping(any(), any()) } returns Result.failure(exception)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.ping()

            val event = turbine.awaitItem()
            assertTrue(event is TemplateReplaceMeWindowsEvent.ShowSnackbar)
            assertTrue((event as TemplateReplaceMeWindowsEvent.ShowSnackbar).message.contains("Failed"))
            assertFalse(viewModel.uiState.value.isLoading)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `ping sets isLoading true then false on success`() = runCoroutineTest {
        val pong = PongModel(id = PingPong("id-2"), firstName = "Jane", lastName = "Smith")
        coEvery { pingPongManager.ping(any(), any()) } returns Result.success(pong)

        viewModel.ping()

        assertFalse(viewModel.uiState.value.isLoading)
    }
}
