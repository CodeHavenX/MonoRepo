package com.cramsan.flyerboard.client.lib.features.main.flyer_submit

import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Flyer Submit screen.
 */
@FrontendViewModel
class FlyerSubmitViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<FlyerSubmitEvent, FlyerSubmitUIState>(dependencies, FlyerSubmitUIState.Initial, TAG) {
    private var selectedFileBytes: ByteArray? = null
    private var selectedMimeType: String? = null
    private var submitting = false

    /**
     * Update the title field in the UI state.
     */
    fun onTitleChanged(title: String) {
        viewModelCoroutineScope.launch { updateUiState { it.copy(title = title) } }
    }

    /**
     * Update the description field in the UI state.
     */
    fun onDescriptionChanged(description: String) {
        viewModelCoroutineScope.launch { updateUiState { it.copy(description = description) } }
    }

    /**
     * Update the expiresAt field in the UI state.
     */
    fun onExpiresAtChanged(expiresAt: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(expiresAt = expiresAt.takeIf { it.isNotBlank() }) }
        }
    }

    /**
     * Store the file selected by the user and update [FlyerSubmitUIState.selectedFileName] for display.
     */
    fun onFileSelected(bytes: ByteArray, name: String, mime: String) {
        logI(TAG, "onFileSelected: $name")
        selectedFileBytes = bytes
        selectedMimeType = mime
        viewModelCoroutineScope.launch { updateUiState { it.copy(selectedFileName = name) } }
    }

    /**
     * Submit the new flyer. No-op when a submission is already in progress.
     */
    fun submit() {
        if (submitting) return
        submitting = true
        logI(TAG, "submit")
        viewModelCoroutineScope.launch {
            val state = uiState.value
            updateUiState { it.copy(status = SubmitStatus.Submitting) }
            flyerManager
                .createFlyer(
                    title = state.title,
                    description = state.description,
                    expiresAt = state.expiresAt,
                    fileBytes = selectedFileBytes ?: ByteArray(0),
                    mimeType = selectedMimeType.orEmpty(),
                ).onSuccess {
                    submitting = false
                    updateUiState { it.copy(status = SubmitStatus.Idle) }
                    emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
                }.onFailure { error ->
                    submitting = false
                    updateUiState { it.copy(status = SubmitStatus.Failed(error.message.orEmpty())) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Failed to submit flyer: ${error.message}"),
                    )
                }
        }
    }

    /**
     * Navigate back without submitting.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelCoroutineScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "FlyerSubmitViewModel"
    }
}
