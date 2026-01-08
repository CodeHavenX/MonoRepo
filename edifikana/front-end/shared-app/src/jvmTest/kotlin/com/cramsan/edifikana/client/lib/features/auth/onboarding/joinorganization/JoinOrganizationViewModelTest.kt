package com.cramsan.edifikana.client.lib.features.auth.onboarding.joinorganization

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [JoinOrganizationViewModel].
 *
 * @see CoroutineTest
 */
class JoinOrganizationViewModelTest : CoroutineTest() {

    private lateinit var viewModel: JoinOrganizationViewModel

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

        viewModel = JoinOrganizationViewModel(
            dependencies = dependencies,
        )
    }

    @Test
    fun `test initial ui state`() = runCoroutineTest {
        assertEquals("", viewModel.uiState.value.organizationNameOrCode)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test onBackSelected emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            // Set up
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onBackSelected()

            // Assert
            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test onOrganizationNameOrCodeChanged updates UI state`() = runCoroutineTest {
        turbineScope {
            // Set up
            val turbine = viewModel.uiState.testIn(backgroundScope)
            assertEquals("", turbine.awaitItem().organizationNameOrCode)

            // Act
            viewModel.onOrganizationNameOrCodeChanged("acme-properties")

            // Assert
            assertEquals("acme-properties", turbine.awaitItem().organizationNameOrCode)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test onCreateNewWorkspaceClicked emits navigation event`() = runCoroutineTest {
        turbineScope {
            // Set up
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onCreateNewWorkspaceClicked()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.CreateNewOrgDestination),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test onJoinOrganizationClicked does not throw`() = runCoroutineTest {
        // Act - should not throw
        viewModel.onJoinOrganizationClicked()

        // Assert - no exception means the test passes
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }
}
