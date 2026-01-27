package com.cramsan.edifikana.client.lib.features.debug.main

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.edifikana.client.lib.features.debug.DebugDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.configuration.PropertyValueType
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel for the Debug screen.
 **/
class DebugViewModel(
    dependencies: ViewModelDependencies,
    private val preferencesManager: PreferencesManager,
    private val haltUtil: HaltUtil,
) : BaseViewModel<DebugEvent, DebugUIModelUI>(
    dependencies,
    DebugUIModelUI.Initial,
    TAG,
) {

    // Buffered changes
    private var bufferedKey: SettingKey<*>? = null
    private var bufferedValue: PropertyValue? = null

    /**
     * Load the initial state of the screen.
     */
    fun loadData() {
        loadDataImpl()
        viewModelScope.launch {
            // Listen to changes in preferences
            preferencesManager.modifiedKey.collect { key ->
                logI(TAG, "Preference key modified: $key")
                loadDataImpl()
            }
        }
    }

    /**
     * Save the value of the field.
     */
    fun saveValue(key: SettingKey<*>, value: Any) {
        viewModelScope.launch {
            saveValueImpl(key, value)
        }
    }

    private suspend fun saveValueImpl(key: SettingKey<*>, value: Any) {
        val propertyValue = getPropertyValue(key, value)
        preferencesManager.updatePreference(key, propertyValue)
        bufferedKey = null
        bufferedValue = null
        logI(TAG, "Debug key $key changed to $value")
        loadDataImpl()
        emitWindowEvent(
            EdifikanaWindowsEvent.ShowSnackbar("Value saved.Restart the app to apply changes."),
        )
    }

    private fun getPropertyValue(key: SettingKey<*>, value: Any): PropertyValue = when (key.type) {
        is PropertyValueType.StringType -> {
            PropertyValue.StringValue((value as String).trim())
        }

        is PropertyValueType.BooleanType -> {
            PropertyValue.BooleanValue(value as Boolean)
        }

        else -> {
            error("Unsupported key type: ${key.type} for key: $key")
        }
    }

    /**
     * Buffer changes to be saved later. This is useful for data that changes frequently and we
     * dont want to save on each change. A common example is a text field or a dial.
     */
    fun bufferChanges(key: SettingKey<*>, value: Any) {
        this.bufferedKey = key
        this.bufferedValue = getPropertyValue(key, value)
    }

    /**
     * Save the buffered changes.
     */
    fun saveBufferedChanges() {
        bufferedKey?.let { key ->
            bufferedValue?.let { value ->
                this.dependencies.appScope.launch {
                    // Dispatch this change to the app scope to avoid blocking the UI thread
                    // and to prevent it from being cancelled if the ViewModel is cleared.
                    saveValueImpl(key, value)
                }
            }
        }
    }

    /**
     * Clear all the preferences.
     */
    fun clearPreferences() {
        viewModelScope.launch {
            preferencesManager.clearPreferences()
            loadDataImpl()
            emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Preferences cleared. Don't forget to restart the app."))
        }
    }

    /**
     * Force closing the application.
     */
    fun closeApplication() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Closing application..."))
            delay(2.seconds)
            haltUtil.crashApp()
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack,
            )
        }
    }

    @Suppress("MaxLineLength", "MaximumLineLength", "LongMethod")
    private fun loadDataImpl() {
        viewModelScope.launch {
            val supabaseOverrideUrl = preferencesManager.getStringPreference(
                EdifikanaSettingKey.SupabaseOverrideUrl,
            ).getOrNull()
            val supabaseOverrideKey = preferencesManager.getStringPreference(
                EdifikanaSettingKey.SupabaseOverrideKey,
            ).getOrNull()
            val haltOnFailure = preferencesManager.getBooleanPreference(
                FrontEndApplicationSettingKey.HaltOnFailure,
            ).getOrNull()
            val openDebugWindow = preferencesManager.getBooleanPreference(
                EdifikanaSettingKey.OpenDebugWindow,
            ).getOrNull()
            val edifikanaBeUrl = preferencesManager.getStringPreference(
                EdifikanaSettingKey.EdifikanaBeUrl,
            ).getOrNull()
            val loggingSeverity = preferencesManager.getStringPreference(
                FrontEndApplicationSettingKey.LoggingLevel,
            ).getOrNull()

            updateUiState {
                it.copy(
                    fields = listOf(
                        Field.Divider,
                        Field.Label(
                            "Supabase Settings",
                            "These settings are used to override the default Supabase settings.",
                        ),
                        Field.StringField(
                            title = "Supabase API URL",
                            subtitle = "To find the api url, run `supabase status` in terminal",
                            key = EdifikanaSettingKey.SupabaseOverrideUrl,
                            value = supabaseOverrideUrl.orEmpty(),
                        ),
                        Field.StringField(
                            title = "Supabase Publishable Key",
                            subtitle = "To find the publishable key, run `supabase status` in terminal",
                            key = EdifikanaSettingKey.SupabaseOverrideKey,
                            value = supabaseOverrideKey.orEmpty(),
                            secure = true,
                        ),
                        Field.Divider,
                        Field.Label(
                            "Edifikana BackEnd Settings",
                            "These settings are used to override the default Back End settings.",
                        ),
                        Field.StringField(
                            title = "Edifikana Back End URL",
                            subtitle = "Provide an override URL",
                            key = EdifikanaSettingKey.EdifikanaBeUrl,
                            value = edifikanaBeUrl.orEmpty(),
                        ),
                        Field.Divider,
                        Field.Label("Core Framework Settings"),
                        Field.BooleanField(
                            title = "Halt on failure",
                            subtitle = "Enable the halt-on-faiilure mechanism when in a supported platform. " +
                                "This will cause the application to freeze when an error is found. Allowing " +
                                "you the chance to connect the debugger and inspect tha application state.",
                            key = FrontEndApplicationSettingKey.HaltOnFailure,
                            value = haltOnFailure ?: false,
                        ),
                        Field.Divider,
                        Field.StringField(
                            title = "Logging Severity",
                            subtitle = null,
                            key = FrontEndApplicationSettingKey.LoggingLevel,
                            value = loggingSeverity.orEmpty(),
                        ),
                        Field.ActionField(
                            title = "Clear all Preferences",
                            subtitle = "Remember to start the app again to apply changes.",
                            action = {
                                emitEvent(DebugEvent.ClearPreferences)
                            },
                        ),
                        Field.ActionField(
                            title = "Close the Application",
                            subtitle = null,
                            action = {
                                emitEvent(DebugEvent.CloseApplication)
                            },
                        ),
                        Field.Divider,
                        Field.BooleanField(
                            title = "Open Debug Window",
                            subtitle = "Currently only supported on desktop.",
                            key = EdifikanaSettingKey.OpenDebugWindow,
                            value = openDebugWindow ?: false,
                        ),
                        Field.Divider,
                        Field.ActionField(
                            title = "Open Screen Selector",
                            subtitle = null,
                            action = {
                                emitWindowEvent(
                                    EdifikanaWindowsEvent.NavigateToScreen(
                                        DebugDestination.ScreenSelectorDestination,
                                    ),
                                )
                            },
                        ),
                    ),
                )
            }
        }
    }

    /**
     * Run an action in the ViewModel scope.
     * This is useful for actions that need to be run in the ViewModel scope.
     */
    fun runAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            action()
        }
    }

    companion object {
        private const val TAG = "DebugViewModel"
    }
}
