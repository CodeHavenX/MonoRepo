package com.cramsan.architecture.client.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [SettingRegistryImpl].
 */
class SettingRegistryTest {

    private fun buildGroup(name: String) = settingGroup(name) {}

    @Test
    fun `new registry has no groups`() {
        val registry = SettingRegistryImpl()

        assertTrue(registry.groups.isEmpty())
    }

    @Test
    fun `register adds one group`() {
        val registry = SettingRegistryImpl()
        val group = buildGroup("G1")

        registry.register(group)

        assertEquals(1, registry.groups.size)
        assertEquals("G1", registry.groups[0].name)
    }

    @Test
    fun `register with two groups adds both in order`() {
        val registry = SettingRegistryImpl()

        registry.register(buildGroup("G1"), buildGroup("G2"))

        assertEquals(listOf("G1", "G2"), registry.groups.map { it.name })
    }

    @Test
    fun `two separate register calls accumulate groups in insertion order`() {
        val registry = SettingRegistryImpl()

        registry.register(buildGroup("G1"))
        registry.register(buildGroup("G2"))

        assertEquals(listOf("G1", "G2"), registry.groups.map { it.name })
    }

    @Test
    fun `groups returns a snapshot not backed by the registry`() {
        val registry = SettingRegistryImpl()
        registry.register(buildGroup("G1"))

        val snapshot = registry.groups.toMutableList()
        snapshot.add(buildGroup("Intruder"))

        assertEquals(1, registry.groups.size, "Mutating the snapshot must not affect the registry")
    }

    @Test
    fun `register preserves sub-group and descriptor structure`() {
        val registry = SettingRegistryImpl()
        val group = settingGroup("Framework") {
            subGroup("Logging") {
                setting(FrontEndApplicationSettingKey.LoggingLevel, "Level")
            }
        }

        registry.register(group)

        val stored = registry.groups[0]
        assertEquals("Framework", stored.name)
        assertEquals("Logging", stored.subGroups[0].name)
        assertEquals(FrontEndApplicationSettingKey.LoggingLevel, stored.subGroups[0].descriptors[0].key)
    }
}
