package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * It is recommended to use the [CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runCoroutineTest` function to run your tests.
 *
 * @see CoroutineTest
 */
@Suppress("UNCHECKED_CAST")
class CreateNewOrgViewModelTest : CoroutineTest() {

    private lateinit var viewModel: CreateNewOrgViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var organizationManager: OrganizationManager

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        organizationManager = mockk(relaxed = true)
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )

        viewModel = CreateNewOrgViewModel(
            dependencies = dependencies,
            organizationManager = organizationManager,
        )
    }

    @Test
    fun `test initial ui state`() = runCoroutineTest {
        assertEquals("", viewModel.uiState.value.organizationName)
        assertEquals("", viewModel.uiState.value.organizationDescription)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test onOrganizationNameChanged updates ui state`() = runCoroutineTest {
        // Act
        viewModel.onOrganizationNameChanged("Acme Properties")

        // Assert
        assertEquals("Acme Properties", viewModel.uiState.value.organizationName)
    }

    @Test
    fun `test onOrganizationDescriptionChanged updates ui state`() = runCoroutineTest {
        // Act
        viewModel.onOrganizationDescriptionChanged("A property management company")

        // Assert
        assertEquals("A property management company", viewModel.uiState.value.organizationDescription)
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
    fun `test onCreateOrganizationClicked success emits NavigateToNavGraph event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val orgName = "Acme Properties"
            val orgDescription = "A property management company"
            coEvery {
                organizationManager.createOrganization(orgName, orgDescription)
            } returns Result.success(mockk())

            viewModel.onOrganizationNameChanged(orgName)
            viewModel.onOrganizationDescriptionChanged(orgDescription)

            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onCreateOrganizationClicked()

            // Assert
            coVerify { organizationManager.createOrganization(orgName, orgDescription) }
            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearTop = true,
                ),
                turbine.awaitItem()
            )
            assertFalse(viewModel.uiState.value.isLoading)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test onCreateOrganizationClicked failure emits ShowSnackbar event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val orgName = "Acme Properties"
            val orgDescription = "A property management company"
            coEvery {
                organizationManager.createOrganization(orgName, orgDescription)
            } returns Result.failure(Exception("Network error"))

            viewModel.onOrganizationNameChanged(orgName)
            viewModel.onOrganizationDescriptionChanged(orgDescription)

            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onCreateOrganizationClicked()

            // Assert
            coVerify { organizationManager.createOrganization(orgName, orgDescription) }
            assertEquals(
                EdifikanaWindowsEvent.ShowSnackbar("Failed to create organization. Please try again."),
                turbine.awaitItem()
            )
            assertFalse(viewModel.uiState.value.isLoading)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
