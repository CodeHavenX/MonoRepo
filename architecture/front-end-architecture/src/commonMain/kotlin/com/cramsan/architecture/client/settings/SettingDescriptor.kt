package com.cramsan.architecture.client.settings

import com.cramsan.framework.configuration.PropertyValueType

/**
 * Represents a top-level grouping of settings by domain (e.g. "Framework", "Edifikana").
 *
 * @property name Human-readable name shown as a section header in the settings UI.
 * @property subGroups Ordered list of sub-groups inside this domain.
 */
data class SettingGroup(val name: String, val subGroups: List<SettingSubGroup>)

/**
 * A named sub-group within a [SettingGroup] (e.g. "Logging", "Network").
 *
 * @property name Human-readable sub-group name shown as a secondary header.
 * @property descriptors Ordered list of setting descriptors in this sub-group.
 */
data class SettingSubGroup(val name: String, val descriptors: List<SettingDescriptor<*>>)

/**
 * Pairs an existing [SettingKey] with display metadata for use in the settings UI.
 *
 * Creating a descriptor does not change how the key is used elsewhere — it is purely additive.
 *
 * @param T The [PropertyValueType] of the underlying key.
 * @property key The typed key used to read/write the preference.
 * @property label Short human-readable label (e.g. "Logging level").
 * @property subtitle Optional longer description shown as a subtitle in the UI.
 */
data class SettingDescriptor<T : PropertyValueType>(
    val key: SettingKey<T>,
    val label: String,
    val subtitle: String? = null,
)
