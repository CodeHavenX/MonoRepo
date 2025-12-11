package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * It is recommended to use the [CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runCoroutineTest` function to run your tests.
 *
 * @see CoroutineTest
 */
@Suppress("UNCHECKED_CAST")
class PropertiesOverviewViewModelTest : CoroutineTest() {

    private lateinit var viewModel: PropertiesOverviewViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var stringProvider: StringProvider

    private lateinit var propertyManager: PropertyManager

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        propertyManager = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )

        viewModel = PropertiesOverviewViewModel(
            dependencies = dependencies,
            propertyManager = propertyManager,
        )
    }

    @Test
    fun `test initial ui state`() = runCoroutineTest {
        // Set up
        coEvery { propertyManager.getPropertyList() } returns Result.success(listOf(
            PropertyModel(
                id = PropertyId("property-1"),
                name = "Test Property 1",
                address = "123 Main St",
                organizationId = OrganizationId("org-1"),
            )
        ))

        // Act
        val initialState = viewModel.uiState.value
        viewModel.initialize()
        val loadedState = viewModel.uiState.value

        // Assert
        assertEquals(true, initialState.isLoading)
        assertEquals(0, initialState.propertyList.size)
        assertEquals(false, loadedState.isLoading)
        assertEquals(1, loadedState.propertyList.size)
        val propertyItem = loadedState.propertyList[0]
        assertEquals(PropertyId("property-1"), propertyItem.id)
        assertEquals("Test Property 1", propertyItem.name)
        assertEquals("123 Main St", propertyItem.address)
        assertNull(propertyItem.imageUrl)
    }
}
