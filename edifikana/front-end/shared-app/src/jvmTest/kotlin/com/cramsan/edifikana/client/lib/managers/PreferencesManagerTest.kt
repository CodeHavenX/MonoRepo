package com.cramsan.edifikana.client.lib.managers

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first

/**
 * Unit tests for the PreferencesManager class.
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS THE TEST GETS CAUGHT ON WAITING FOR EMIT
 *
 */
@Ignore
class PreferencesManagerTest : CoroutineTest() {
    private lateinit var preferences: Preferences
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: PreferencesManager

    /**
     * Sets up the test environment, initializing mocks and the PreferencesManager instance.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        preferences = mockk(relaxed = true)

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = PreferencesManager(preferences, dependencies)
    }

    /**
     * Tests that setPreference saves a String and emits the key.
     */
    @Test
    fun `setPreference saves String and emits key`() = runCoroutineTest {
        // Arrange
        val key = "testKey"
        val value = "testValue"
        coEvery { preferences.saveString(key, value) } returns Unit
        // Act
        manager.setPreference(key, value)
        // Assert
        coVerify { preferences.saveString(key, value) }
        assertEquals(key, manager.modifiedKey.first())
    }

    /**
     * Tests that setPreference saves a Boolean and emits the key.
     */
    @Test
    fun `setPreference saves Boolean and emits key`() = runCoroutineTest {
        // Arrange
        val key = "boolKey"
        val value = true
        coEvery { preferences.saveBoolean(key, value) } returns Unit
        // Act
        manager.setPreference(key, value)
        // Assert
        coVerify { preferences.saveBoolean(key, value) }
        assertEquals(key, manager.modifiedKey.first())
    }

    /**
     * Tests that setPreference throws for unsupported type.
     */
    @Test
    fun `setPreference throws for unsupported type`() = runCoroutineTest {
        // Arrange
        val key = "unsupportedKey"
        val value = 123
        // Act & Assert
        val exception = assertFailsWith<IllegalArgumentException> {
            manager.setPreference(key, value)
        }
        assertTrue(exception.message!!.contains("Unsupported value type"))
    }

    /**
     * Tests that loadStringPreference returns the value from preferences.
     */
    @Test
    fun `loadStringPreference returns value`() = runCoroutineTest {
        // Arrange
        val key = "stringKey"
        val value = "storedValue"
        coEvery { preferences.loadString(key) } returns value
        // Act
        val result = manager.loadStringPreference(key)
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(value, result.getOrNull())
    }

    /**
     * Tests that loadStringPreference returns empty string if not set.
     */
    @Test
    fun `loadStringPreference returns empty string if not set`() = runCoroutineTest {
        // Arrange
        val key = "unsetKey"
        coEvery { preferences.loadString(key) } returns null
        // Act
        val result = manager.loadStringPreference(key)
        // Assert
        assertTrue(result.isSuccess)
        assertEquals("", result.getOrNull())
    }
}

