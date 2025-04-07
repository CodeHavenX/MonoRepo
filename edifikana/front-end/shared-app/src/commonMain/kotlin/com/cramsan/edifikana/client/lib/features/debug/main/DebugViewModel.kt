package com.cramsan.edifikana.client.lib.features.debug.main

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.framework.preferences.Preferences
import kotlinx.coroutines.launch

/**
 * ViewModel for the Debug screen.
 **/
class DebugViewModel(
    dependencies: ViewModelDependencies,
    private val preferences: Preferences,
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
    }

    /**
     * Save the value of the field.
     */
    fun saveValue(key: String, value: Any) {
        viewModelScope.launch {
            when (value) {
                is String -> preferences.saveString(key, value)
                is Boolean -> preferences.saveBoolean(key, value)
                else -> throw IllegalArgumentException("Unsupported value type: $value with key $key")
            }
            bufferedKey = null
            bufferedValue = null
            logI(TAG, "Debug key $key changed to $value")
            loadDataImpl()
            emitEvent(
                DebugEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.ShowSnackbar("Value saved")
                )
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
            emitEvent(
                DebugEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.NavigateBack
                )
            )
        }
    }

    private fun loadDataImpl() {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    fields = listOf(
                        Field.Label("Application Settings"),
                        Field.BooleanField(
                            title = "Disable Supabase(requires restart)",
                            subtitle = "This will allow this client to use fake a Supabase dependency.",
                            key = Overrides.KEY_DISABLE_SUPABASE,
                            value = preferences.loadBoolean(Overrides.KEY_DISABLE_SUPABASE) ?: false,
                        ),
                        Field.BooleanField(
                            title = "Disable BackEnd(requires restart)",
                            subtitle = "This will allow this client not make calls to the BE.",
                            key = Overrides.KEY_DISABLE_BE,
                            value = preferences.loadBoolean(Overrides.KEY_DISABLE_BE) ?: false,
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
                            value = preferences.loadBoolean(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED) ?: false,
                        ),
                        Field.StringField(
                            title = "Supabase URL",
                            subtitle = "Provide an override URL",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_URL,
                            value = preferences.loadString(Overrides.KEY_SUPABASE_OVERRIDE_URL) ?: "",
                        ),
                        Field.StringField(
                            title = "Supabase Anon Key",
                            subtitle = "Provide an override Api Anon Key",
                            key = Overrides.KEY_SUPABASE_OVERRIDE_KEY,
                            value = preferences.loadString(Overrides.KEY_SUPABASE_OVERRIDE_KEY) ?: "",
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
                            value = preferences.loadBoolean(Overrides.KEY_HALT_ON_FAILURE) ?: false,
                        ),
                    ),
                )
            }
        }
    }

    companion object {
        private const val TAG = "DebugViewModel"
    }
}
