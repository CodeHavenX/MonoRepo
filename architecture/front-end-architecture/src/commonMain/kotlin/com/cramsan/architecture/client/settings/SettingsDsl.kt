package com.cramsan.architecture.client.settings

import com.cramsan.framework.configuration.PropertyValueType

/**
 * DSL marker annotation preventing illegal nesting of settings DSL builders.
 */
@DslMarker
annotation class SettingsDsl

/**
 * Builder for a [SettingSubGroup]. Collects [SettingDescriptor]s via [setting] calls.
 *
 * @property name Name of the sub-group being built.
 */
@SettingsDsl
class SettingSubGroupBuilder internal constructor(private val name: String) {
    private val descriptors = mutableListOf<SettingDescriptor<*>>()

    /**
     * Adds a [SettingDescriptor] for the given [key] to this sub-group.
     *
     * @param key The typed setting key.
     * @param label Short human-readable label for this setting.
     * @param subtitle Optional longer description shown below the label.
     */
    fun <T : PropertyValueType> setting(
        key: SettingKey<T>,
        label: String,
        subtitle: String? = null,
    ) {
        descriptors += SettingDescriptor(key = key, label = label, subtitle = subtitle)
    }

    internal fun build() = SettingSubGroup(name = name, descriptors = descriptors.toList())
}

/**
 * Builder for a [SettingGroup]. Collects [SettingSubGroup]s via [subGroup] calls.
 *
 * @property name Name of the domain group being built.
 */
@SettingsDsl
class SettingGroupBuilder internal constructor(private val name: String) {
    private val subGroups = mutableListOf<SettingSubGroup>()

    /**
     * Adds a sub-group with the given [name] to this domain group.
     *
     * @param name Human-readable name for the sub-group.
     * @param block DSL block that populates the sub-group with settings.
     */
    fun subGroup(name: String, block: SettingSubGroupBuilder.() -> Unit) {
        subGroups += SettingSubGroupBuilder(name).apply(block).build()
    }

    internal fun build() = SettingGroup(name = name, subGroups = subGroups.toList())
}

/**
 * DSL entry point for defining a [SettingGroup] with its sub-groups and individual settings.
 *
 * Example:
 * ```kotlin
 * settingGroup("Logging") {
 *     subGroup("Console") {
 *         setting(LoggingLevel, "Logging Level", "VERBOSE / DEBUG / INFO / WARNING / ERROR")
 *         setting(LoggingEnableFileLogging, "Enable File Logging")
 *     }
 * }
 * ```
 *
 * @param name Human-readable name for the top-level domain group.
 * @param block DSL block that populates the group with sub-groups.
 * @return A fully constructed [SettingGroup].
 */
fun settingGroup(name: String, block: SettingGroupBuilder.() -> Unit): SettingGroup =
    SettingGroupBuilder(name).apply(block).build()
