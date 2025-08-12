package com.cramsan.edifikana.client.lib.features.debug.main

import com.cramsan.edifikana.client.lib.features.debug.DebugDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PreferencesManager
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Debug screen.
 **/
class DebugViewModel(
    dependencies: ViewModelDependencies,
    private val preferencesManager: PreferencesManager,
) : BaseViewModel<DebugEvent, DebugUIModelUI>(
    dependencies,
    DebugUIModelUI.Initial,
    TAG,
) {

    // Buffered changes
    private var bufferedKey: String? = null
    private var bufferedValue: Any? = null

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
    fun saveValue(key: String, value: Any) {
        viewModelScope.launch {
            preferencesManager.setPreference(key, value)
            bufferedKey = null
            bufferedValue = null
            logI(TAG, "Debug key $key changed to $value")
            loadDataImpl()
            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar("Value saved.Restart the app to apply changes.")
            )
        }
    }

    /**
     * Buffer changes to be saved later. This is useful for data that changes frequently and we
     * dont want to save on each change. A common example is a text field or a dial.
     */
    fun bufferChanges(key: String, value: Any) {
        this.bufferedKey = key
        this.bufferedValue = value
    }

    /**
     * Save the buffered changes.
     */
    fun saveBufferedChanges() {
        bufferedKey?.let { key ->
            bufferedValue?.let { value ->
                saveValue(key, value)
            }
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    @Suppress("MaxLineLength", "MaximumLineLength", "LongMethod")
    private fun loadDataImpl() {
        viewModelScope.launch {
            val disableSupabase = preferencesManager.isSupabaseDisabled().getOrThrow()
            val disableBE = preferencesManager.isBackendDisabled().getOrThrow()
            val supabaseOverrideEnabled = preferencesManager.isSupabaseOverrideEnabled().getOrThrow()
            val supabaseOverrideUrl = preferencesManager.getSupabaseOverrideUrl().getOrThrow()
            val supabaseOverrideKey = preferencesManager.getSupabaseOverrideKey().getOrThrow()
            val haltOnFailure = preferencesManager.haltOnFailure().getOrThrow()
            val openDebugWindow = preferencesManager.isOpenDebugWindow().getOrThrow()
            val edifikanaBeOverrideEnabled = preferencesManager.isEdifikanaBackendOverrideEnabled().getOrThrow()
            val edifikanaBeUrl = preferencesManager.getEdifikanaBackendUrl().getOrThrow()
            val loggingSeverity = preferencesManager.loggingSeverityOverride().getOrThrow()

            updateUiState {
                it.copy(
                    fields = listOf(
                        Field.Label("Application Settings"),
                        Field.BooleanField(
                            title = "Disable Supabase",
                            subtitle = "This will allow this client to use fake a Supabase dependency.",
                            key = Overrides.KEY_DISABLE_SUPABASE.name,
                            value = disableSupabase
                        ),
                        Field.BooleanField(
                            title = "Disable BackEnd(requires restart)",
                            subtitle = "This will allow this client not make calls to the BE.",
                            key = Overrides.KEY_DISABLE_BE.name,
                            value = disableBE,
                        ),
                        Field.Divider,
                        Field.Label(
                            "Supabase Settings",
                            "These settings are used to override the default Supabase settings.",
                        ),
                        Field.BooleanField(
                            title = "Enable the override settings",
                            subtitle = "Toggle this to enable the override settings.",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_ENABLED.name,
                            value = supabaseOverrideEnabled,
                        ),
                        Field.StringField(
                            title = "Supabase URL",
                            subtitle = "Provide an override URL",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_URL.name,
                            value = supabaseOverrideUrl,
                        ),
                        Field.StringField(
                            title = "Supabase Anon Key",
                            subtitle = "Provide an override Api Anon Key",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_KEY.name,
                            value = supabaseOverrideKey,
                            secure = true,
                        ),
                        Field.Divider,
                        Field.Label(
                            "Edifikana BackEnd Settings",
                            "These settings are used to override the default Back End settings.",
                        ),
                        Field.BooleanField(
                            title = "Enable the override settings",
                            subtitle = "Enable this to override the default Back End settings.",
                            key = Overrides.KEY_EDIFIKANA_BE_OVERRIDE_ENABLED.name,
                            value = edifikanaBeOverrideEnabled,
                        ),
                        Field.StringField(
                            title = "Edifikana Back End URL",
                            subtitle = "Provide an override URL",
                            key = Overrides.KEY_EDIFIKANA_BE_URL.name,
                            value = edifikanaBeUrl,
                        ),
                        Field.Divider,
                        Field.Label("Core Framework Settings"),
                        Field.BooleanField(
                            title = "Halt on failure",
                            subtitle = "Enable the halt-on-faiilure mechanism when in a supported platform. " +
                                "This will cause the application to freeze when an error is found. Allowing " +
                                "you the chance to connect the debugger and inspect tha application state.",
                            key = Overrides.KEY_HALT_ON_FAILURE.name,
                            value = haltOnFailure,
                        ),
                        Field.Divider,
                        Field.StringField(
                            title = "Logging Severity",
                            subtitle = null,
                            key = Overrides.KEY_LOGGING_SEVERITY_OVERRIDE.name,
                            value = loggingSeverity,
                        ),
                        Field.BooleanField(
                            title = "Open Debug Window",
                            subtitle = "Currently only supported on desktop.",
                            key = Overrides.KEY_OPEN_DEBUG_WINDOW.name,
                            value = openDebugWindow,
                            enabled = false, // Disabled while we set up the required infrastructure
                        ),
                        Field.Divider,
                        Field.ActionField(
                            title = "Open Screen Selector",
                            subtitle = null,
                            action = {
                                emitWindowEvent(
                                    EdifikanaWindowsEvent.NavigateToScreen(
                                        DebugDestination.ScreenSelectorDestination,
                                    )
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
