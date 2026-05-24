package com.cramsan.framework.sample.shared.features.main.assertutil

import app.cash.turbine.turbineScope
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AssertUtilViewModelTest : CoroutineTest() {

    private lateinit var assertUtil: AssertUtilInterface
    private lateinit var viewModel: AssertUtilViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        assertUtil = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = AssertUtilViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            assertUtil = assertUtil,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(AssertUtilUIState.Initial, viewModel.uiState.value)
    }

    @Test
    fun `assertTrue calls assert with true`() = runCoroutineTest {
        justRun { assertUtil.assert(true, any(), any()) }

        viewModel.assertTrue()

        verify { assertUtil.assert(true, any(), any()) }
    }

    @Test
    fun `assertFalse calls assert with false`() = runCoroutineTest {
        justRun { assertUtil.assert(false, any(), any()) }

        viewModel.assertFalse()

        verify { assertUtil.assert(false, any(), any()) }
    }

    @Test
    fun `assertFalsePasses calls assertFalse with false`() = runCoroutineTest {
        justRun { assertUtil.assertFalse(false, any(), any()) }

        viewModel.assertFalsePasses()

        verify { assertUtil.assertFalse(false, any(), any()) }
    }

    @Test
    fun `assertFalseFails calls assertFalse with true`() = runCoroutineTest {
        justRun { assertUtil.assertFalse(true, any(), any()) }

        viewModel.assertFalseFails()

        verify { assertUtil.assertFalse(true, any(), any()) }
    }

    @Test
    fun `assertNullPasses calls assertNull with null`() = runCoroutineTest {
        justRun { assertUtil.assertNull(null, any(), any()) }

        viewModel.assertNullPasses()

        verify { assertUtil.assertNull(null, any(), any()) }
    }

    @Test
    fun `assertNotNullPasses calls assertNotNull with non-null value`() = runCoroutineTest {
        justRun { assertUtil.assertNotNull(any(), any(), any()) }

        viewModel.assertNotNullPasses()

        verify { assertUtil.assertNotNull(any(), any(), any()) }
    }

    @Test
    fun `assertFailure calls assertFailure`() = runCoroutineTest {
        justRun { assertUtil.assertFailure(any(), any()) }

        viewModel.assertFailure()

        verify { assertUtil.assertFailure(any(), any()) }
    }

    @Test
    fun `navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(SampleWindowEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
