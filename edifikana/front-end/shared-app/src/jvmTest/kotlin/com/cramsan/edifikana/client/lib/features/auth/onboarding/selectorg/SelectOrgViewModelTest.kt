package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for [SelectOrgViewModel].
 *
 * @see CoroutineTest
 */
class SelectOrgViewModelTest : CoroutineTest() {

    private lateinit var viewModel: SelectOrgViewModel

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

        viewModel = SelectOrgViewModel(
            dependencies = dependencies,
        )
    }

    @Test
    fun `test initial ui state`() = runCoroutineTest {
        assertEquals(SelectOrgUIState, viewModel.uiState.value)
    }

    @Test
    fun `test loadContent does not throw`() = runCoroutineTest {
        // Act - should not throw
        viewModel.loadContent()
    }

    @Test
    fun `test joinTeam does not throw`() = runCoroutineTest {
        // Act - should not throw
        viewModel.joinTeam()
    }

    @Test
    fun `test createWorkspace does not throw`() = runCoroutineTest {
        // Act - should not throw
        viewModel.createWorkspace()
    }

    @Test
    fun `test signOut throws NotImplementedError`() = runCoroutineTest {
        // Act & Assert
        assertFailsWith<NotImplementedError> {
            viewModel.signOut()
        }
    }
}
