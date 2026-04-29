package com.cramsan.framework.preferences

import com.cramsan.framework.test.CoroutineTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * @Author cramsan
 * @created 1/16/2021
 */
abstract class PreferencesTest : CoroutineTest() {
    private lateinit var _preferences: Preferences
    protected val preferences: Preferences get() = _preferences

    /** Returns the [Preferences] instance to test. Called before each test. */
    protected abstract fun createPreferences(): Preferences

    /** Initializes [preferences] before each test by calling [createPreferences]. */
    @BeforeTest
    fun setUpPreferences() {
        _preferences = createPreferences()
    }

    /** Clears preferences after each test. */
    @AfterTest
    fun tearDown() {
        preferences.clear()
    }

    /**
     * Save a string and try to read it.
     */
    @Test
    fun testSaveString() {
        preferences.saveString("Hello1", "Test2")

        val result = preferences.loadString("Hello1")

        assertEquals("Test2", result)
    }

    /**
     * Save an int and try to read it.
     */
    @Test
    fun testSaveInt() {
        preferences.saveInt("Hello1", 3)

        val result = preferences.loadInt("Hello1")

        assertEquals(3, result)
    }

    /**
     * Save a long and try to read it.
     */
    @Test
    fun testSaveLong() {
        preferences.saveLong("Hello1", 2)

        val result = preferences.loadLong("Hello1")

        assertEquals(2, result)
    }

    /**
     * Save a boolean and try to read it.
     */
    @Test
    fun testSaveBoolean() {
        preferences.saveBoolean("Hello1", true)

        val result = preferences.loadBoolean("Hello1")

        assertEquals(true, result)
    }

    /**
     * Remove a previously saved key.
     */
    @Test
    fun testRemove() {
        preferences.saveString("Hello1", "Test2")

        preferences.remove("Hello1")

        assertNull(preferences.loadString("Hello1"))
    }

    /**
     * Clear all preferences and verify keys are gone.
     */
    @Test
    fun testClear() {
        preferences.saveString("key1", "value1")
        preferences.saveInt("key2", 42)

        preferences.clear()

        assertNull(preferences.loadString("key1"))
        assertNull(preferences.loadInt("key2"))
    }
}
