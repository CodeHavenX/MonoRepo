package com.cramsan.templatereplaceme.client.lib.app

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
import com.cramsan.templatereplaceme.client.lib.managers.InitializerManager
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [TemplateReplaceMeApplicationViewModel].
 */
class TemplateReplaceMeApplicationViewModelTest : CoroutineTest() {

    private lateinit var viewModel: TemplateReplaceMeApplicationViewModel
    private lateinit var initializer: InitializerManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        initializer = mockk<InitializerManager>(relaxed = true)
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = TemplateReplaceMeApplicationViewModel(
            initHandler = initializer,
            dependencies = dependencies,
        )
    }

    @Test
    fun `initial state has showDebugWindow false`() = runCoroutineTest {
        assertFalse(viewModel.uiState.value.showDebugWindow)
    }
}
