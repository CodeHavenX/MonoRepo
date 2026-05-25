package com.cramsan.architecture.client.features.debugsettings

import app.cash.turbine.turbineScope
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.SettingRegistryImpl
import com.cramsan.architecture.client.settings.settingGroup
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.configuration.PropertyValue
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
import kotlin.test.assertTrue

/**
 * Unit tests for [DebugSettingsViewModel].
 */
class DebugSettingsViewModelTest : CoroutineTest() {

    private lateinit var registry: SettingRegistryImpl
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        AssertUtil.setInstance(NoopAssertUtil())
        registry = SettingRegistryImpl()
        preferencesManager = mockk(relaxed = true)
        windowEventBus = EventBus()
    }

    private fun buildViewModel(isDebugBuild: Boolean): DebugSettingsViewModel {
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = CollectorCoroutineExceptionHandler(),
            windowEventReceiver = windowEventBus,
            applicationEventReceiver = EventBus<ApplicationEvent>(),
            isDebugBuild = isDebugBuild,
        )
        return DebugSettingsViewModel(dependencies, registry, preferencesManager)
    }

    @Test
    fun `loadSettings with isDebugBuild false does not populate groups`() = runCoroutineTest {
        val viewModel = buildViewModel(isDebugBuild = false)

        viewModel.loadSettings()

        assertTrue(viewModel.uiState.value.groups.isEmpty())
    }

    @Test
    fun `loadSettings with isDebugBuild true and empty registry produces empty groups`() = runCoroutineTest {
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.loadSettings()

        assertTrue(viewModel.uiState.value.groups.isEmpty())
    }

    @Test
    fun `loadSettings maps boolean setting to BooleanRow with correct value`() = runCoroutineTest {
        coEvery { preferencesManager.getBooleanPreference(FrontEndApplicationSettingKey.IsDebug) } returns
            Result.success(true)
        registry.register(
            settingGroup("Test") {
                subGroup("Sub") {
                    setting(FrontEndApplicationSettingKey.IsDebug, "Debug Mode")
                }
            },
        )
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.loadSettings()

        val row = viewModel.uiState.value.groups[0].subGroups[0].rows[0]
        assertTrue(row is SettingRowUIModel.BooleanRow)
        assertTrue((row as SettingRowUIModel.BooleanRow).currentValue)
    }

    @Test
    fun `loadSettings maps string setting to StringRow with correct value`() = runCoroutineTest {
        coEvery { preferencesManager.getStringPreference(FrontEndApplicationSettingKey.BackEndUrl) } returns
            Result.success("https://example.com")
        registry.register(
            settingGroup("Test") {
                subGroup("Net") {
                    setting(FrontEndApplicationSettingKey.BackEndUrl, "URL")
                }
            },
        )
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.loadSettings()

        val row = viewModel.uiState.value.groups[0].subGroups[0].rows[0]
        assertTrue(row is SettingRowUIModel.StringRow)
        assertEquals("https://example.com", (row as SettingRowUIModel.StringRow).currentValue)
    }

    @Test
    fun `loadSettings maps int setting to IntRow with value as string`() = runCoroutineTest {
        val intKey = SettingKey.int("test.int")
        coEvery { preferencesManager.getIntPreference(intKey) } returns Result.success(42)
        registry.register(
            settingGroup("Test") {
                subGroup("Sub") {
                    setting(intKey, "Int Setting")
                }
            },
        )
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.loadSettings()

        val row = viewModel.uiState.value.groups[0].subGroups[0].rows[0]
        assertTrue(row is SettingRowUIModel.IntRow)
        assertEquals("42", (row as SettingRowUIModel.IntRow).currentValue)
    }

    @Test
    fun `loadSettings with unset long preference shows empty string`() = runCoroutineTest {
        val longKey = SettingKey.long("test.long")
        coEvery { preferencesManager.getLongPreference(longKey) } returns Result.success(null)
        registry.register(
            settingGroup("Test") {
                subGroup("Sub") {
                    setting(longKey, "Long Setting")
                }
            },
        )
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.loadSettings()

        val row = viewModel.uiState.value.groups[0].subGroups[0].rows[0]
        assertTrue(row is SettingRowUIModel.LongRow)
        assertEquals("", (row as SettingRowUIModel.LongRow).currentValue)
    }

    @Test
    fun `loadSettings with unset boolean preference defaults to false`() = runCoroutineTest {
        coEvery { preferencesManager.getBooleanPreference(FrontEndApplicationSettingKey.IsDebug) } returns
            Result.success(null)
        registry.register(
            settingGroup("Test") {
                subGroup("Sub") {
                    setting(FrontEndApplicationSettingKey.IsDebug, "Debug Mode")
                }
            },
        )
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.loadSettings()

        val row = viewModel.uiState.value.groups[0].subGroups[0].rows[0]
        assertFalse((row as SettingRowUIModel.BooleanRow).currentValue)
    }

    @Test
    fun `saveValue with boolean calls updatePreference with BooleanValue`() = runCoroutineTest {
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.saveValue(FrontEndApplicationSettingKey.IsDebug, true)

        coVerify { preferencesManager.updatePreference(FrontEndApplicationSettingKey.IsDebug, PropertyValue.BooleanValue(true)) }
    }

    @Test
    fun `saveValue with string calls updatePreference with trimmed StringValue`() = runCoroutineTest {
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.saveValue(FrontEndApplicationSettingKey.BackEndUrl, "  https://example.com  ")

        coVerify { preferencesManager.updatePreference(FrontEndApplicationSettingKey.BackEndUrl, PropertyValue.StringValue("https://example.com")) }
    }

    @Test
    fun `saveValue with valid int string calls updatePreference with IntValue`() = runCoroutineTest {
        val intKey = SettingKey.int("test.int")
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.saveValue(intKey, "42")

        coVerify { preferencesManager.updatePreference(intKey, PropertyValue.IntValue(42)) }
    }

    @Test
    fun `saveValue with empty int string saves IntValue zero without crashing`() = runCoroutineTest {
        val intKey = SettingKey.int("test.int")
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.saveValue(intKey, "")

        coVerify { preferencesManager.updatePreference(intKey, PropertyValue.IntValue(0)) }
    }

    @Test
    fun `saveValue with non-numeric long string saves LongValue zero without crashing`() = runCoroutineTest {
        val longKey = SettingKey.long("test.long")
        val viewModel = buildViewModel(isDebugBuild = true)

        viewModel.saveValue(longKey, "abc")

        coVerify { preferencesManager.updatePreference(longKey, PropertyValue.LongValue(0L)) }
    }

    @Test
    fun `saveValue refreshes UI state after saving`() = runCoroutineTest {
        coEvery { preferencesManager.getBooleanPreference(FrontEndApplicationSettingKey.IsDebug) } returnsMany
            listOf(Result.success(false), Result.success(true))
        registry.register(
            settingGroup("Test") {
                subGroup("Sub") {
                    setting(FrontEndApplicationSettingKey.IsDebug, "Debug Mode")
                }
            },
        )
        val viewModel = buildViewModel(isDebugBuild = true)
        viewModel.loadSettings()

        viewModel.saveValue(FrontEndApplicationSettingKey.IsDebug, true)

        val row = viewModel.uiState.value.groups[0].subGroups[0].rows[0] as SettingRowUIModel.BooleanRow
        assertTrue(row.currentValue)
    }

    @Test
    fun `saveValue emits ShowSnackbar event`() = runCoroutineTest {
        val viewModel = buildViewModel(isDebugBuild = true)

        turbineScope {
            val turbine = viewModel.events.testIn(backgroundScope)

            viewModel.saveValue(FrontEndApplicationSettingKey.IsDebug, false)

            val event = turbine.awaitItem()
            assertTrue(event is DebugSettingsEvent.ShowSnackbar)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
