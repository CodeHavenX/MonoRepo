package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import com.cramsan.flyerboard.client.lib.features.main.flyer_edit.FlyerEditUIState.Editing
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Flyer Edit screen.
 */
@FrontendViewModel
class FlyerEditViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<FlyerEditEvent, FlyerEditUIState>(dependencies, FlyerEditUIState.Initial, TAG) {
    private var selectedFileBytes: ByteArray? = null
    private var selectedMimeType: String? = null

    /**
     * Load the flyer identified by [flyerIdValue] and populate the form fields.
     */
    fun loadFlyer(flyerIdValue: String) {
        logI(TAG, "loadFlyer: $flyerIdValue")
        viewModelCoroutineScope.launch {
            updateUiState { FlyerEditUIState.Loading }
            flyerManager
                .getFlyer(FlyerId(flyerIdValue))
                .onSuccess { flyer ->
                    if (flyer == null) {
                        updateUiState { FlyerEditUIState.Loading }
                        emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer not found."))
                    } else {
                        updateUiState {
                            Editing(
                                title = flyer.title,
                                description = flyer.description,
                                expiresAt = flyer.expiresAt,
                                errorMessage = null,
                                selectedFileName = null,
                                isSaving = false,
                            )
                        }
                    }
                }.onFailure { error ->
                    updateUiState { FlyerEditUIState.Loading }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Failed to load flyer: ${error.message}"),
                    )
                }
        }
    }

    /**
     * Update the title field in the UI state.
     */
    fun onTitleChanged(title: String) {
        viewModelCoroutineScope.launch {
            updateUiState { state -> (state as? Editing)?.copy(title = title) ?: state }
        }
    }

    /**
     * Update the description field in the UI state.
     */
    fun onDescriptionChanged(description: String) {
        viewModelCoroutineScope.launch {
            updateUiState { state -> (state as? Editing)?.copy(description = description) ?: state }
        }
    }

    /**
     * Store the file selected by the user and update [FlyerEditUIState.Editing.selectedFileName] for display.
     */
    fun onFileSelected(bytes: ByteArray, name: String, mime: String) {
        logI(TAG, "onFileSelected: $name")
        selectedFileBytes = bytes
        selectedMimeType = mime
        viewModelCoroutineScope.launch {
            updateUiState { state -> (state as? Editing)?.copy(selectedFileName = name) ?: state }
        }
    }

    /**
     * Update the expiresAt field in the UI state.
     */
    fun onExpiresAtChanged(expiresAt: String) {
        viewModelCoroutineScope.launch {
            updateUiState { state ->
                (state as? Editing)?.copy(expiresAt = expiresAt.takeIf { it.isNotBlank() }) ?: state
            }
        }
    }

    /**
     * Submit the edited flyer fields for [flyerIdValue].
     */
    fun saveFlyer(flyerIdValue: String) {
        logI(TAG, "saveFlyer: $flyerIdValue")
        viewModelCoroutineScope.launch {
            val editingState = uiState.value as? Editing ?: return@launch
            updateUiState { state -> (state as? Editing)?.copy(isSaving = true, errorMessage = null) ?: state }
            flyerManager
                .updateFlyer(
                    flyerId = FlyerId(flyerIdValue),
                    title = editingState.title,
                    description = editingState.description,
                    expiresAt = editingState.expiresAt,
                    fileBytes = selectedFileBytes,
                    mimeType = selectedMimeType,
                ).onSuccess {
                    updateUiState { state -> (state as? Editing)?.copy(isSaving = false) ?: state }
                    emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
                }.onFailure { error ->
                    updateUiState { state ->
                        (state as? Editing)?.copy(isSaving = false, errorMessage = error.message) ?: state
                    }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Failed to save flyer: ${error.message}"),
                    )
                }
        }
    }

    /**
     * Navigate back without saving.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelCoroutineScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "FlyerEditViewModel"
    }
}
