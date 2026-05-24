package com.cramsan.framework.sample.shared.features.main.userevents

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.userevents.UserEventsInterface
import kotlinx.coroutines.launch

/**
 * ViewModel for the UserEvents screen.
 */
@FrontendViewModel
class UserEventsViewModel(dependencies: ViewModelDependencies, private val userEvents: UserEventsInterface) :
    BaseViewModel<UserEventsEvent, UserEventsUIState>(
        dependencies,
        UserEventsUIState.Initial,
        TAG,
    ) {
    /**
     * Call UserEventsInterface.initialize().
     */
    fun initialize() {
        userEvents.initialize()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "initialize() called") }
        }
    }

    /**
     * Call log(tag, event) without metadata.
     */
    fun logEvent() {
        userEvents.log(TAG, "sample_event")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "log(tag, \"sample_event\") called") }
        }
    }

    /**
     * Call log(tag, event, metadata) with a sample metadata map.
     */
    fun logEventWithMetadata() {
        userEvents.log(TAG, "sample_event_with_metadata", mapOf("key" to "value", "source" to "sample"))
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(lastAction = "log(tag, \"sample_event_with_metadata\", metadata) called") }
        }
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "UserEventsViewModel"
    }
}
