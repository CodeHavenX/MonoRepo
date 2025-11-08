package com.cramsan.edifikana.client.lib.managers

import app.cash.turbine.test
import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the PreferencesManager class.
 */
class PreferencesManagerTest : CoroutineTest() {
    private lateinit var settingsHolder: SettingsHolder
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: PreferencesManager

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        settingsHolder = mockk(relaxed = true)

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = PreferencesManager(settingsHolder, dependencies)
    }

    @Test
    fun `haltOnFailure returns value from settingsHolder when present`() = runCoroutineTest {
        coEvery { settingsHolder.getBoolean(FrontEndApplicationSettingKey.HaltOnFailure) } returns true

        val result = manager.haltOnFailure()

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify { settingsHolder.getBoolean(FrontEndApplicationSettingKey.HaltOnFailure) }
    }

    @Test
    fun `haltOnFailure defaults to false when not set`() = runCoroutineTest {
        coEvery { settingsHolder.getBoolean(FrontEndApplicationSettingKey.HaltOnFailure) } returns null

        val result = manager.haltOnFailure()

        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun `updatePreference saves value and emits modified key`() = runCoroutineTest {
        val key = EdifikanaSettingKey.SupabaseOverrideUrl
        val value = PropertyValue.StringValue("https://example.com")

        coEvery { settingsHolder.saveValue(key, value) } returns Unit

        // collect the first emitted key from the flow using a CompletableDeferred and launch
        val verificationJob = launch { manager.modifiedKey.test {
            assertEquals(EdifikanaSettingKey.SupabaseOverrideUrl, awaitItem())
        } }

        manager.updatePreference(key, value)

        coVerify { settingsHolder.saveValue(key, value) }
        verificationJob.join()
    }

    @Test
    fun `getSupabaseOverrideUrl and key and enabled return stored values or defaults`() = runCoroutineTest {
        coEvery { settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideUrl) } returns "https://supa"
        coEvery { settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideKey) } returns "key123"

        val url = manager.getSupabaseOverrideUrl()
        val key = manager.getSupabaseOverrideKey()

        assertTrue(url.isSuccess)
        assertEquals("https://supa", url.getOrNull())
        assertTrue(key.isSuccess)
        assertEquals("key123", key.getOrNull())
    }

    @Test
    fun `edifikana backend urls and flags return stored values or defaults`() = runCoroutineTest {
        coEvery { settingsHolder.getString(EdifikanaSettingKey.EdifikanaBeUrl) } returns "https://be"
        coEvery { settingsHolder.getBoolean(EdifikanaSettingKey.OpenDebugWindow) } returns true
        coEvery { settingsHolder.getString(FrontEndApplicationSettingKey.LoggingLevel) } returns "DEBUG"

        val be = manager.getEdifikanaBackendUrl()
        val debug = manager.isOpenDebugWindow()
        val logging = manager.loggingSeverityOverride()

        assertTrue(be.isSuccess)
        assertEquals("https://be", be.getOrNull())
        assertTrue(debug.isSuccess)
        assertEquals(true, debug.getOrNull())
        assertTrue(logging.isSuccess)
        assertEquals("DEBUG", logging.getOrNull())
    }

    @Test
    fun `getSupabaseOverrideUrl default values when not set`() = runCoroutineTest {
        coEvery { settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideUrl) } returns null
        coEvery { settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideKey) } returns null

        val url = manager.getSupabaseOverrideUrl()
        val key = manager.getSupabaseOverrideKey()

        assertTrue(url.isSuccess)
        assertEquals("", url.getOrNull())
        assertTrue(key.isSuccess)
        assertEquals("", key.getOrNull())
    }

    @Test
    fun `edifikana backend and logging defaults when not set`() = runCoroutineTest {
        coEvery { settingsHolder.getString(EdifikanaSettingKey.EdifikanaBeUrl) } returns null
        coEvery { settingsHolder.getBoolean(EdifikanaSettingKey.OpenDebugWindow) } returns null
        coEvery { settingsHolder.getString(FrontEndApplicationSettingKey.LoggingLevel) } returns null

        val be = manager.getEdifikanaBackendUrl()
        val debug = manager.isOpenDebugWindow()
        val logging = manager.loggingSeverityOverride()

        assertTrue(be.isSuccess)
        assertEquals("", be.getOrNull())
        assertTrue(debug.isSuccess)
        assertEquals(false, debug.getOrNull())
        assertTrue(logging.isSuccess)
        assertEquals("INFO", logging.getOrNull())
    }

    @Test
    fun `clearPreferences calls settingsHolder clearAllPreferences`() = runCoroutineTest {
        coEvery { settingsHolder.clearAllPreferences() } returns Unit

        val res = manager.clearPreferences()

        assertTrue(res.isSuccess)
        coVerify { settingsHolder.clearAllPreferences() }
    }
}
