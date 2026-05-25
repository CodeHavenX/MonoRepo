package com.cramsan.architecture.client.features.debugsettings

import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.framework.configuration.PropertyValueType
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Top-level UI state for the debug settings screen.
 *
 * @property isDebugBuild Whether the current build is a debug build. When false the screen shows
 *   a placeholder instead of real settings, providing a safety net against accidental release access.
 * @property groups Ordered list of display-ready setting groups to render.
 */
data class DebugSettingsUIState(val groups: List<SettingGroupUIModel>) : ViewModelUIState {
    companion object {
        /** Sensible starting state before [DebugSettingsViewModel.loadSettings] completes. */
        val Initial = DebugSettingsUIState(groups = emptyList())
    }
}

/**
 * Display model for one [com.cramsan.architecture.client.settings.SettingGroup].
 *
 * @property name Group header label.
 * @property subGroups Ordered sub-groups to render under this header.
 */
data class SettingGroupUIModel(val name: String, val subGroups: List<SettingSubGroupUIModel>)

/**
 * Display model for one [com.cramsan.architecture.client.settings.SettingSubGroup].
 *
 * @property name Sub-group header label.
 * @property rows The individual setting rows to display.
 */
data class SettingSubGroupUIModel(val name: String, val rows: List<SettingRowUIModel>)

/**
 * A single editable row in the debug settings list.
 *
 * Each subtype carries the typed [SettingKey] so the ViewModel can pass it back when saving.
 */
sealed class SettingRowUIModel {
    /**
     * A boolean toggle row rendered as a [Switch].
     *
     * @property key Typed key for this boolean setting.
     * @property label Short display label.
     * @property subtitle Optional description shown below the label.
     * @property currentValue Current persisted value (false if unset).
     */
    data class BooleanRow(
        val key: SettingKey<PropertyValueType.BooleanType>,
        val label: String,
        val subtitle: String?,
        val currentValue: Boolean,
    ) : SettingRowUIModel()

    /**
     * A free-text row rendered as an [OutlinedTextField].
     *
     * @property key Typed key for this string setting.
     * @property label Short display label.
     * @property subtitle Optional description shown below the label.
     * @property currentValue Current persisted value (empty string if unset).
     */
    data class StringRow(
        val key: SettingKey<PropertyValueType.StringType>,
        val label: String,
        val subtitle: String?,
        val currentValue: String,
    ) : SettingRowUIModel()

    /**
     * A numeric text row for integer settings.
     *
     * @property key Typed key for this integer setting.
     * @property label Short display label.
     * @property subtitle Optional description shown below the label.
     * @property currentValue Current persisted value as a string (empty if unset).
     */
    data class IntRow(
        val key: SettingKey<PropertyValueType.IntType>,
        val label: String,
        val subtitle: String?,
        val currentValue: String,
    ) : SettingRowUIModel()

    /**
     * A numeric text row for long integer settings.
     *
     * @property key Typed key for this long setting.
     * @property label Short display label.
     * @property subtitle Optional description shown below the label.
     * @property currentValue Current persisted value as a string (empty if unset).
     */
    data class LongRow(
        val key: SettingKey<PropertyValueType.LongType>,
        val label: String,
        val subtitle: String?,
        val currentValue: String,
    ) : SettingRowUIModel()
}
