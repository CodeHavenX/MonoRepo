package com.cramsan.edifikana.client.lib.features.admin.property

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.utils.loginvalidation.validateEmail
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the Property screen.
 **/
class PropertyViewModel(
    private val propertyManager: PropertyManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<PropertyEvent, PropertyUIState>(dependencies, PropertyUIState.Empty, TAG) {

    private var propertyId: PropertyId? = null

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
                    address = property.address
                )
            }
        }
    }

    /**
     * Add a new manager to the property.
     */
    fun addManager(email: String) {
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

    /**
     * Remove a manager from the property.
     */
    fun removeManager(email: String) {
        updateUiState {
            it.copy(
                managers = it.managers - email
            )
        }
    }

    companion object {
        private const val TAG = "PropertyViewModel"
    }
}
