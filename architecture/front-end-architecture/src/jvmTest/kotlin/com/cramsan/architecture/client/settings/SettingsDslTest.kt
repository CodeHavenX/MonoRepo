package com.cramsan.architecture.client.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the [settingGroup] DSL and its associated builder classes.
 */
class SettingsDslTest {

    @Test
    fun `settingGroup creates group with correct name`() {
        val group = settingGroup("MyGroup") {}

        assertEquals("MyGroup", group.name)
    }

    @Test
    fun `empty block produces group with no sub-groups`() {
        val group = settingGroup("Empty") {}

        assertTrue(group.subGroups.isEmpty())
    }

    @Test
    fun `subGroup creates sub-group with correct name`() {
        val group = settingGroup("Domain") {
            subGroup("MySub") {}
        }

        assertEquals(1, group.subGroups.size)
        assertEquals("MySub", group.subGroups[0].name)
    }

    @Test
    fun `subGroup with no settings produces empty descriptors`() {
        val group = settingGroup("Domain") {
            subGroup("Empty") {}
        }

        assertTrue(group.subGroups[0].descriptors.isEmpty())
    }

    @Test
    fun `setting adds descriptor with correct key and label`() {
        val group = settingGroup("Domain") {
            subGroup("Sub") {
                setting(FrontEndApplicationSettingKey.LoggingLevel, "Level")
            }
        }

        val descriptor = group.subGroups[0].descriptors[0]
        assertEquals(FrontEndApplicationSettingKey.LoggingLevel, descriptor.key)
        assertEquals("Level", descriptor.label)
    }

    @Test
    fun `setting with subtitle carries subtitle`() {
        val group = settingGroup("Domain") {
            subGroup("Sub") {
                setting(FrontEndApplicationSettingKey.LoggingLevel, "Level", "my hint")
            }
        }

        assertEquals("my hint", group.subGroups[0].descriptors[0].subtitle)
    }

    @Test
    fun `setting without subtitle leaves it null`() {
        val group = settingGroup("Domain") {
            subGroup("Sub") {
                setting(FrontEndApplicationSettingKey.LoggingLevel, "Level")
            }
        }

        assertNull(group.subGroups[0].descriptors[0].subtitle)
    }

    @Test
    fun `multiple subGroup calls produce sub-groups in insertion order`() {
        val group = settingGroup("Domain") {
            subGroup("A") {}
            subGroup("B") {}
            subGroup("C") {}
        }

        assertEquals(listOf("A", "B", "C"), group.subGroups.map { it.name })
    }

    @Test
    fun `multiple setting calls produce descriptors in insertion order`() {
        val group = settingGroup("Domain") {
            subGroup("Sub") {
                setting(FrontEndApplicationSettingKey.LoggingLevel, "Level")
                setting(FrontEndApplicationSettingKey.LoggingEnableFileLogging, "File")
            }
        }

        val descriptors = group.subGroups[0].descriptors
        assertEquals(FrontEndApplicationSettingKey.LoggingLevel, descriptors[0].key)
        assertEquals(FrontEndApplicationSettingKey.LoggingEnableFileLogging, descriptors[1].key)
    }

    @Test
    fun `multiple key types are supported in the same sub-group`() {
        val group = settingGroup("Domain") {
            subGroup("Mixed") {
                setting(FrontEndApplicationSettingKey.LoggingLevel, "String key")
                setting(FrontEndApplicationSettingKey.IsDebug, "Boolean key")
                setting(SettingKey.int("int.key"), "Int key")
                setting(SettingKey.long("long.key"), "Long key")
            }
        }

        assertEquals(4, group.subGroups[0].descriptors.size)
    }
}
