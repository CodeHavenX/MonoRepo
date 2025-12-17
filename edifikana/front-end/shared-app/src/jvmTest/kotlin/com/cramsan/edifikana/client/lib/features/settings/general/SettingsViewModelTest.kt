package com.cramsan.edifikana.client.lib.features.settings.general

import app.cash.turbine.test
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
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
import com.cramsan.ui.components.themetoggle.SelectedTheme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SettingsViewModelTest : CoroutineTest() {

    private lateinit var viewModel: SettingsViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var preferencesManager: PreferencesManager

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        preferencesManager = mockk(relaxed = true)
        viewModel = SettingsViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            preferencesManager = preferencesManager,
        )
    }


    @Test
    fun `initialize loads selected theme from preferences`() = runCoroutineTest {
        // Arrange: make preferences return DARK
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.SelectedTheme) } returns Result.success("DARK")

        // Act
        viewModel.initialize()

        // Assert
        assertEquals(SelectedTheme.DARK, viewModel.uiState.value.selectedTheme)
    }

    @Test
    fun `changeSelectedTheme persists preference and updates ui state`() = runCoroutineTest {
        // Arrange
        coEvery { preferencesManager.updatePreference(EdifikanaSettingKey.SelectedTheme, "LIGHT") } returns Result.success(Unit)

        // Act
        viewModel.changeSelectedTheme(SelectedTheme.LIGHT)

        // Assert
        coVerify { preferencesManager.updatePreference(EdifikanaSettingKey.SelectedTheme, "LIGHT") }
        assertEquals(SelectedTheme.LIGHT, viewModel.uiState.value.selectedTheme)
    }

    @Test
    fun `navigateBack emits NavigateBack window event`() = runCoroutineTest {
        // Set up turbine to listen to window events
        val job = launch {
            windowEventBus.events.test {
                assertEquals(com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent.NavigateBack, awaitItem())
                // ensure collector drains
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        // Act
        viewModel.navigateBack()

        // Assert
        job.join()
    }

    @Test
    fun `modifiedKey emission triggers reload of selected theme`() = runCoroutineTest {
        // Create a mutable flow to simulate modifiedKey emissions
        val modifiedFlow = MutableSharedFlow<SettingKey<*>>()
        every { preferencesManager.modifiedKey } returns modifiedFlow

        // Make getStringPreference return LIGHT first, then DARK on subsequent call
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.SelectedTheme) } returnsMany listOf(Result.success("LIGHT"), Result.success("DARK"))

        // Act: initialize will load first value and start collector
        viewModel.initialize()
        assertEquals(SelectedTheme.LIGHT, viewModel.uiState.value.selectedTheme)

        // Emit changed key to trigger reload
        launch { modifiedFlow.emit(EdifikanaSettingKey.SelectedTheme) }

        // Assert the state was reloaded to DARK
        testCoroutineScope.advanceUntilIdle()
        assertEquals(SelectedTheme.DARK, viewModel.uiState.value.selectedTheme)
    }

}