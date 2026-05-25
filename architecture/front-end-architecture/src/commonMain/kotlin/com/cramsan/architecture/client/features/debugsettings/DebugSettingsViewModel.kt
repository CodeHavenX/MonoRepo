package com.cramsan.architecture.client.features.debugsettings

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.architecture.client.settings.SettingDescriptor
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.SettingRegistry
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.configuration.PropertyValueType
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the debug settings screen.
 *
 * Reads all registered settings from [SettingRegistry], loads current values via
 * [PreferencesManager], and writes updates back through [PreferencesManager].
 *
 * The screen is gated by [ViewModelDependencies.isDebugBuild]; when false, no preferences
 * are loaded and the UI shows a placeholder instead.
 *
 * @property dependencies Standard ViewModel dependencies, including the debug-build flag.
 * @property settingRegistry Registry of all registered setting groups.
 * @property preferencesManager Manager used to read and write individual preference values.
 */
@FrontendViewModel
class DebugSettingsViewModel(
    dependencies: ViewModelDependencies,
    private val settingRegistry: SettingRegistry,
    private val preferencesManager: PreferencesManager,
) : BaseViewModel<DebugSettingsEvent, DebugSettingsUIState>(
    dependencies,
    DebugSettingsUIState.Initial,
    TAG,
) {
    /**
     * Loads all registered settings and their current persisted values into [uiState].
     *
     * Should be called when the screen becomes visible (e.g. [androidx.lifecycle.Lifecycle.Event.ON_START]).
     * Does nothing if the build is not a debug build.
     */
    fun loadSettings() {
        viewModelCoroutineScope.launch {
            refreshGroups()
        }
    }

    /**
     * Saves a new value for the given [key] and refreshes the UI.
     *
     * Accepts raw [Any] values matched to the key type:
     * - [PropertyValueType.BooleanType] → pass [Boolean]
     * - [PropertyValueType.StringType] → pass [String] (trimmed before saving)
     * - [PropertyValueType.IntType] → pass [String] (parsed to [Int], defaults to 0 on failure)
     * - [PropertyValueType.LongType] → pass [String] (parsed to [Long], defaults to 0 on failure)
     *
     * @param key The typed [SettingKey] whose value should be updated.
     * @param value The new value; must match the type declared by [key].
     */
    fun saveValue(key: SettingKey<*>, value: Any) {
        viewModelCoroutineScope.launch {
            val propertyValue: PropertyValue =
                when (key.type) {
                    is PropertyValueType.StringType -> {
                        PropertyValue.StringValue((value as String).trim())
                    }

                    is PropertyValueType.BooleanType -> {
                        PropertyValue.BooleanValue(value as Boolean)
                    }

                    is PropertyValueType.IntType -> {
                        PropertyValue.IntValue((value as String).trim().toIntOrNull() ?: 0)
                    }

                    is PropertyValueType.LongType -> {
                        PropertyValue.LongValue((value as String).trim().toLongOrNull() ?: 0L)
                    }
                }
            preferencesManager.updatePreference(key, propertyValue)
            refreshGroups()
            emitEvent(DebugSettingsEvent.ShowSnackbar("Saved. Restart the app to apply changes."))
        }
    }

    private suspend fun refreshGroups() {
        val groups =
            settingRegistry.groups.map { group ->
                SettingGroupUIModel(
                    name = group.name,
                    subGroups =
                    group.subGroups.map { subGroup ->
                        SettingSubGroupUIModel(
                            name = subGroup.name,
                            rows =
                            subGroup.descriptors.mapNotNull { descriptor ->
                                buildRow(descriptor)
                            },
                        )
                    },
                )
            }
        updateUiState { it.copy(groups = groups) }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun buildRow(descriptor: SettingDescriptor<*>): SettingRowUIModel? {
        return when (descriptor.key.type) {
            is PropertyValueType.BooleanType -> {
                val key = descriptor.key as SettingKey<PropertyValueType.BooleanType>
                val value = preferencesManager.getBooleanPreference(key).getOrNull() ?: false
                SettingRowUIModel.BooleanRow(
                    key = key,
                    label = descriptor.label,
                    subtitle = descriptor.subtitle,
                    currentValue = value,
                )
            }

            is PropertyValueType.StringType -> {
                val key = descriptor.key as SettingKey<PropertyValueType.StringType>
                val value = preferencesManager.getStringPreference(key).getOrNull().orEmpty()
                SettingRowUIModel.StringRow(
                    key = key,
                    label = descriptor.label,
                    subtitle = descriptor.subtitle,
                    currentValue = value,
                )
            }

            is PropertyValueType.IntType -> {
                val key = descriptor.key as SettingKey<PropertyValueType.IntType>
                val value =
                    preferencesManager
                        .getIntPreference(key)
                        .getOrNull()
                        ?.toString()
                        .orEmpty()
                SettingRowUIModel.IntRow(
                    key = key,
                    label = descriptor.label,
                    subtitle = descriptor.subtitle,
                    currentValue = value,
                )
            }

            is PropertyValueType.LongType -> {
                val key = descriptor.key as SettingKey<PropertyValueType.LongType>
                val value =
                    preferencesManager
                        .getLongPreference(key)
                        .getOrNull()
                        ?.toString()
                        .orEmpty()
                SettingRowUIModel.LongRow(
                    key = key,
                    label = descriptor.label,
                    subtitle = descriptor.subtitle,
                    currentValue = value,
                )
            }
        }
    }

    companion object {
        private const val TAG = "DebugSettingsViewModel"
    }
}
