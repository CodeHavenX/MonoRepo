package com.cramsan.framework.sample.shared.features.main.configuration

import app.cash.turbine.turbineScope
import com.cramsan.framework.configuration.Configuration
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
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConfigurationViewModelTest : CoroutineTest() {

    companion object {
        private const val SAMPLE_STRING = "hello"
        private const val SAMPLE_INT = 42
        private const val SAMPLE_LONG = 100L
        private const val SAMPLE_BOOLEAN = true
    }

    private lateinit var configuration: Configuration
    private lateinit var viewModel: ConfigurationViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        configuration = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = ConfigurationViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            configuration = configuration,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(ConfigurationUIState.Initial, viewModel.uiState.value)
        assertTrue(viewModel.uiState.value is ConfigurationUIState.NotRead)
    }

    @Test
    fun `readString updates stringValue`() = runCoroutineTest {
        every { configuration.readString(any()) } returns SAMPLE_STRING

        viewModel.readString()

        val state = viewModel.uiState.value
        assertTrue(state is ConfigurationUIState.Read)
        assertEquals(SAMPLE_STRING, (state as ConfigurationUIState.Read).stringValue)
    }

    @Test
    fun `readString with null result sets stringValue to null`() = runCoroutineTest {
        every { configuration.readString(any()) } returns null

        viewModel.readString()

        val state = viewModel.uiState.value
        assertTrue(state is ConfigurationUIState.Read)
        assertNull((state as ConfigurationUIState.Read).stringValue)
    }

    @Test
    fun `readInt updates intValue`() = runCoroutineTest {
        every { configuration.readInt(any()) } returns SAMPLE_INT

        viewModel.readInt()

        val state = viewModel.uiState.value
        assertTrue(state is ConfigurationUIState.Read)
        assertEquals(SAMPLE_INT, (state as ConfigurationUIState.Read).intValue)
    }

    @Test
    fun `readLong updates longValue`() = runCoroutineTest {
        every { configuration.readLong(any()) } returns SAMPLE_LONG

        viewModel.readLong()

        val state = viewModel.uiState.value
        assertTrue(state is ConfigurationUIState.Read)
        assertEquals(SAMPLE_LONG, (state as ConfigurationUIState.Read).longValue)
    }

    @Test
    fun `readBoolean updates booleanValue`() = runCoroutineTest {
        every { configuration.readBoolean(any()) } returns SAMPLE_BOOLEAN

        viewModel.readBoolean()

        val state = viewModel.uiState.value
        assertTrue(state is ConfigurationUIState.Read)
        assertEquals(SAMPLE_BOOLEAN, (state as ConfigurationUIState.Read).booleanValue)
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
