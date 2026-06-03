package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

/**
 * Tests for [FeatureReplacemeViewModel].
 */
class FeatureReplacemeViewModelTest : CoroutineTest() {

    private lateinit var viewModel: FeatureReplacemeViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = FeatureReplacemeViewModel(dependencies = dependencies)
    }

    @Test
    fun `initial ui state is not loading`() = runCoroutineTest {
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
