package com.cramsan.edifikana.client.lib.features.management.property

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.utils.loginvalidation.validateEmail
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel for the Property screen.
 **/
class PropertyViewModel(
    private val propertyManager: PropertyManager,
    private val staffManager: StaffManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<PropertyEvent, PropertyUIState>(dependencies, PropertyUIState.Empty, TAG) {

    private var propertyId: PropertyId? = null
    private var cachedStaff: List<StaffModel> = emptyList()
    private var suggestionQueryJob: Job? = null

    private var initialState: PropertyUIState? = null

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            if (initialState != null && initialState != uiState.value) {
                emitEvent(PropertyEvent.ShowSaveBeforeExitingDialog)
            } else {
                emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            }
        }
    }

    /**
     * Save changes to the property.
     */
    fun saveChanges(exitAfterSave: Boolean) {
        val propertyId = propertyId ?: return

        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val name = uiState.value.propertyName.orEmpty()
            val address = uiState.value.address.orEmpty()
            propertyManager.updateProperty(propertyId, name, address).onFailure {
                updateUiState { it.copy(isLoading = false) }
                val message = stringProvider.getString(Res.string.error_message_unexpected_error)
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(message)
                )
                return@launch
            }
            updateUiState { it.copy(isLoading = false) }
            emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Changes saved successfully"))
            if (exitAfterSave) {
                emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            }
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
                    title = property.name,
                    propertyName = property.name,
                    address = property.address,
                    staff = listOf(),
                ).also { initialState = it }
            }
            cachedStaff = staffManager.getStaffList().getOrThrow()
        }
    }

    /**
     * Add a new staff to the property.
     */
    fun addStaff(email: String) {
        viewModelScope.launch {
            val isEmailValid = validateEmail(email).isEmpty()
            if (isEmailValid) {
                updateUiState {
                    it.copy(
                        staff = it.staff + StaffUIModel(null, email, false),
                        addStaffError = false,
                        addStaffEmail = "",
                    )
                }
            } else {
                updateUiState {
                    it.copy(
                        addStaffError = true,
                        addStaffEmail = email,
                    )
                }
            }
        }
    }

    /**
     * Remove a staff from the property.
     */
    fun toggleStaffState(staffUIModel: StaffUIModel) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    staff = it.staff.map { staff ->
                        if (staff.email == staffUIModel.email) {
                            staff.copy(isRemoving = staff.isRemoving.not())
                        } else {
                            staff
                        }
                    },
                )
            }
        }
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
                        addStaffEmail = query,
                    )
                }
            }
        }
    }

    /**
     * Show the remove dialog.
     */
    fun showRemoveDialog() {
        viewModelScope.launch {
            emitEvent(PropertyEvent.ShowRemoveDialog)
        }
    }

    /**
     * Confirm the removal of the property.
     */
    fun removeProperty() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            propertyManager.removeProperty(propertyId ?: return@launch).onFailure {
                updateUiState { it.copy(isLoading = false) }
                val message = stringProvider.getString(Res.string.error_message_unexpected_error)
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(message)
                )
                return@launch
            }
            updateUiState { it.copy(isLoading = false) }
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Update the property name in the UI state.
     */
    fun updatePropertyName(name: String) {
        viewModelScope.launch {
            updateUiState { it.copy(propertyName = name) }
        }
    }

    /**
     * Update the property address in the UI state.
     */
    fun updatePropertyAddress(address: String) {
        viewModelScope.launch {
            updateUiState { it.copy(address = address) }
        }
    }

    companion object {
        private const val TAG = "PropertyViewModel"
    }
}
