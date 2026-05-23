package com.cramsan.framework.sample.shared.features.main.preferences

import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PreferencesViewModelTest : CoroutineTest() {

    companion object {
        private const val SAMPLE_STRING = "hello"
        private const val SAMPLE_INT = 42
        private const val SAMPLE_LONG = 100L
        private const val SAMPLE_BOOLEAN = true
    }

    private lateinit var preferences: Preferences
    private lateinit var viewModel: PreferencesViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        preferences = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = PreferencesViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            preferences = preferences,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(PreferencesUIState.Initial, viewModel.uiState.value)
        assertNull(viewModel.uiState.value.stringValue)
        assertNull(viewModel.uiState.value.intValue)
        assertNull(viewModel.uiState.value.longValue)
        assertNull(viewModel.uiState.value.booleanValue)
    }

    @Test
    fun `saveString calls preferences saveString`() = runCoroutineTest {
        justRun { preferences.saveString(any(), any()) }

        viewModel.saveString()

        verify { preferences.saveString(any(), SAMPLE_STRING) }
    }

    @Test
    fun `loadString updates stringValue in UIState`() = runCoroutineTest {
        every { preferences.loadString(any()) } returns SAMPLE_STRING

        viewModel.loadString()

        assertEquals(SAMPLE_STRING, viewModel.uiState.value.stringValue)
    }

    @Test
    fun `saveInt calls preferences saveInt`() = runCoroutineTest {
        justRun { preferences.saveInt(any(), any()) }

        viewModel.saveInt()

        verify { preferences.saveInt(any(), SAMPLE_INT) }
    }

    @Test
    fun `loadInt updates intValue in UIState`() = runCoroutineTest {
        every { preferences.loadInt(any()) } returns SAMPLE_INT

        viewModel.loadInt()

        assertEquals(SAMPLE_INT, viewModel.uiState.value.intValue)
    }

    @Test
    fun `saveLong calls preferences saveLong`() = runCoroutineTest {
        justRun { preferences.saveLong(any(), any()) }

        viewModel.saveLong()

        verify { preferences.saveLong(any(), SAMPLE_LONG) }
    }

    @Test
    fun `loadLong updates longValue in UIState`() = runCoroutineTest {
        every { preferences.loadLong(any()) } returns SAMPLE_LONG

        viewModel.loadLong()

        assertEquals(SAMPLE_LONG, viewModel.uiState.value.longValue)
    }

    @Test
    fun `saveBoolean calls preferences saveBoolean`() = runCoroutineTest {
        justRun { preferences.saveBoolean(any(), any()) }

        viewModel.saveBoolean()

        verify { preferences.saveBoolean(any(), SAMPLE_BOOLEAN) }
    }

    @Test
    fun `loadBoolean updates booleanValue in UIState`() = runCoroutineTest {
        every { preferences.loadBoolean(any()) } returns SAMPLE_BOOLEAN

        viewModel.loadBoolean()

        assertEquals(SAMPLE_BOOLEAN, viewModel.uiState.value.booleanValue)
    }

    @Test
    fun `remove calls preferences remove and clears stringValue`() = runCoroutineTest {
        justRun { preferences.remove(any()) }
        every { preferences.loadString(any()) } returns SAMPLE_STRING
        viewModel.loadString()

        viewModel.remove()

        verify { preferences.remove(any()) }
        assertNull(viewModel.uiState.value.stringValue)
    }

    @Test
    fun `clear calls preferences clear and resets UIState to Initial`() = runCoroutineTest {
        justRun { preferences.clear() }

        viewModel.clear()

        verify { preferences.clear() }
        assertEquals(PreferencesUIState.Initial, viewModel.uiState.value)
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
