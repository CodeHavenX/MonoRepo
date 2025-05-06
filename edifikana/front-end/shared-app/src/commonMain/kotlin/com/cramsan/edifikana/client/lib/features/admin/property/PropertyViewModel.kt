package com.cramsan.edifikana.client.lib.features.admin.property

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.utils.loginvalidation.validateEmail
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the Property screen.
 **/
class PropertyViewModel(
    private val propertyManager: PropertyManager,
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<PropertyEvent, PropertyUIState>(dependencies, PropertyUIState.Empty, TAG) {

    private var propertyId: PropertyId? = null
    private var cachedStaff: List<StaffModel> = emptyList()
    private var suggestionQueryJob: Job? = null

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
        }
    }

    /**
     * Save changes to the property.
     */
    fun saveChanges(name: String, address: String) {
        val propertyId = propertyId ?: return

        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            propertyManager.updateProperty(propertyId, name, address).onFailure {
                updateUiState { it.copy(isLoading = false) }
                val message = getString(Res.string.error_message_unexpected_error)
                emitApplicationEvent(
                    EdifikanaApplicationEvent.ShowSnackbar(message)
                )
                return@launch
            }
            updateUiState { it.copy(isLoading = false) }
        }
    }

    /**
     * Load the content for the property.
     */
    fun loadContent(propertyId: PropertyId) {
        this.propertyId = propertyId
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val property = propertyManager.getProperty(propertyId).getOrThrow()
            updateUiState {
                PropertyUIState.Empty.copy(
                    propertyName = property.name,
                    address = property.address,
                    managers = listOf(),
                )
            }
            cachedStaff = staffManager.getStaffList().getOrThrow()
        }
    }

    /**
     * Add a new manager to the property.
     */
    fun addManager(email: String) {
        viewModelScope.launch {
            val isEmailValid = validateEmail(email).isEmpty()
            if (isEmailValid) {
                updateUiState {
                    it.copy(
                        managers = it.managers + email,
                        addManagerError = false,
                        addManagerEmail = "",
                    )
                }
            } else {
                updateUiState {
                    it.copy(
                        addManagerError = true,
                        addManagerEmail = email,
                    )
                }
            }
        }
    }

    /**
     * Remove a manager from the property.
     */
    fun removeManager(email: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    managers = it.managers - email
                )
            }
        }
    }

    /**
     * Select a suggestion.
     */
    fun selectSuggestion(staffSuggestion: String) {
        addManager(staffSuggestion)
    }

    /**
     * Request new suggestions based on the query.
     */
    @Suppress("MagicNumber")
    fun requestNewSuggestions(query: String) {
        viewModelScope.launch {
            if (query.length < 3) {
                updateUiState {
                    it.copy(
                        suggestions = emptyList(),
                    )
                }
                return@launch
            }
            suggestionQueryJob?.cancel()
            suggestionQueryJob = null
            suggestionQueryJob = viewModelScope.launch {
                val suggestions = cachedStaff
                    .filter { it.email?.contains(query) == true }
                    .mapNotNull { it.email }
                updateUiState {
                    it.copy(
                        suggestions = suggestions,
                        addManagerEmail = query,
                    )
                }
            }
        }
    }

    /**
     * Select a suggestion.
     */
    fun selectSuggestion(staffSuggestion: String) {
        addManager(staffSuggestion)
    }

    /**
     * Request new suggestions based on the query.
     */
    @Suppress("MagicNumber")
    fun requestNewSuggestions(query: String) {
        if (query.length < 3) {
            updateUiState {
                it.copy(
                    suggestions = emptyList(),
                )
            }
            return
        }
        suggestionQueryJob?.cancel()
        suggestionQueryJob = null
        suggestionQueryJob = viewModelScope.launch {
            val suggestions = cachedStaff
                .filter { it.email?.contains(query) == true }
                .mapNotNull { it.email }
            updateUiState {
                it.copy(
                    suggestions = suggestions,
                    addManagerEmail = query,
                )
            }
        }
    }

    companion object {
        private const val TAG = "PropertyViewModel"
    }
}
