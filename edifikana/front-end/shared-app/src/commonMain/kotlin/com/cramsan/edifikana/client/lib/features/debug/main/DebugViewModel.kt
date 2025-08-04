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
                EdifikanaWindowsEvent.ShowSnackbar("Value saved")
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
            val disableSupabase = preferencesManager
                .loadBooleanPreference(Overrides.KEY_DISABLE_SUPABASE).getOrThrow()
            val disableBE = preferencesManager
                .loadBooleanPreference(Overrides.KEY_DISABLE_BE).getOrThrow()
            val supabaseOverrideEnabled = preferencesManager
                .loadBooleanPreference(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED).getOrThrow()
            val supabaseOverrideUrl = preferencesManager
                .loadStringPreference(Overrides.KEY_SUPABASE_OVERRIDE_URL).getOrThrow()
            val supabaseOverrideKey = preferencesManager
                .loadStringPreference(Overrides.KEY_SUPABASE_OVERRIDE_KEY).getOrThrow()
            val haltOnFailure = preferencesManager
                .loadBooleanPreference(Overrides.KEY_HALT_ON_FAILURE).getOrThrow()
            val openDebugWindow = preferencesManager
                .loadBooleanPreference(Overrides.KEY_OPEN_DEBUG_WINDOW).getOrThrow()

            updateUiState {
                it.copy(
                    fields = listOf(
                        Field.Label("Application Settings"),
                        Field.BooleanField(
                            title = "Disable Supabase(requires restart)",
                            subtitle = "This will allow this client to use fake a Supabase dependency.",
                            key = Overrides.KEY_DISABLE_SUPABASE,
                            value = disableSupabase
                        ),
                        Field.BooleanField(
                            title = "Disable BackEnd(requires restart)",
                            subtitle = "This will allow this client not make calls to the BE.",
                            key = Overrides.KEY_DISABLE_BE,
                            value = disableBE,
                        ),
                        Field.Divider,
                        Field.Label(
                            "Supabase Settings",
                            "These settings are used to override the default Supabase settings. (Require restart)",
                        ),
                        Field.BooleanField(
                            title = "Enable the override settings",
                            subtitle = "This will allow this client to use fake dependencies.",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_ENABLED,
                            value = supabaseOverrideEnabled,
                        ),
                        Field.StringField(
                            title = "Supabase URL",
                            subtitle = "Provide an override URL",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_URL,
                            value = supabaseOverrideUrl,
                        ),
                        Field.StringField(
                            title = "Supabase Anon Key",
                            subtitle = "Provide an override Api Anon Key",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_KEY,
                            value = supabaseOverrideKey,
                            secure = true,
                        ),
                        Field.Divider,
                        Field.Label("Core Framework Settings"),
                        Field.BooleanField(
                            title = "Halt on failure",
                            subtitle = "Enable the halt-on-faiilure mechanism when in a supported platform. " +
                                "This will cause the application to freeze when an error is found. Allowing " +
                                "you the chance to connect the debugger and inspect tha application state.",
                            key = Overrides.KEY_HALT_ON_FAILURE,
                            value = haltOnFailure,
                        ),
                        Field.Divider,
                        Field.BooleanField(
                            title = "Open Debug Window",
                            subtitle = "Currently only supported on desktop.",
                            key = Overrides.KEY_OPEN_DEBUG_WINDOW,
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
