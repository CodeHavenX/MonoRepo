package com.cramsan.edifikana.client.lib.features.root.main

import com.cramsan.edifikana.client.lib.features.root.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationDelegatedEvent
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Main activity view model.
 */
class MainActivityViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(MainActivityUiModel("", emptyList()))
    val uiModel: StateFlow<MainActivityUiModel> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainActivityEvent>()
    val events: SharedFlow<MainActivityEvent> = _events.asSharedFlow()

    private val _delegatedEvents = MutableSharedFlow<EdifikanaApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<EdifikanaApplicationDelegatedEvent> = _delegatedEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            delegatedEvents.collect {
                logI(TAG, "Delegated event received: $it")
            }
        }
    }

    /**
     * Handle received image.
     */
    fun handleReceivedImage(uri: CoreUri?) = viewModelScope.launch {
        if (uri == null) {
            logI(TAG, "Uri was null.")
        } else {
            logI(TAG, "Uri was received: $uri")
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleReceivedImage(uri))
        }
    }

    /**
     * Handle received images.
     */
    fun handleReceivedImages(uris: List<CoreUri>) = viewModelScope.launch {
        if (uris.isEmpty()) {
            logI(TAG, "Uri list is empty.")
        } else {
            logI(TAG, "Uri list received with ${uris.count()} elements.")
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleReceivedImages(uris))
        }
    }

    /**
     * Execute main activity event.
     */
    fun executeMainActivityEvent(event: MainActivityEvent) = viewModelScope.launch {
        _events.emit(event)
    }

    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        logI(TAG, "Navigating to account page.")
        viewModelScope.launch {
            _events.emit(
                MainActivityEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.AccountDestination)
                )
            )
        }
    }

    /**
     * Navigate to the admin page.
     */
    fun navigateToAdmin() {
        logI(TAG, "Navigating to admin page.")
        viewModelScope.launch {
            _events.emit(
                MainActivityEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.AdminDestination)
                )
            )
        }
    }

    fun loadProperties() {
        logI(TAG, "Loading properties.")
        viewModelScope.launch {
            updatePropertyList()
        }
    }

    fun selectProperty(propertyId: PropertyId) {
        logI(TAG, "Property selected: $propertyId")
        viewModelScope.launch {
            propertyManager.setActiveProperty(propertyId)
            updatePropertyList()
        }
    }

    private suspend fun updatePropertyList() {
        val properties = propertyManager.getPropertyList().getOrThrow()
        val selectedProperty = propertyManager.activeProperty().value
        var name = ""
        _uiState.update {
            val propertyUiModels = properties.map { property ->
                val isSelected = property.id == selectedProperty
                if (isSelected) {
                    name = property.name
                }
                property.toUIModel(selected = isSelected)
            }
            it.copy(
                label = name,
                availableProperties = propertyUiModels,
            )
        }

    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
