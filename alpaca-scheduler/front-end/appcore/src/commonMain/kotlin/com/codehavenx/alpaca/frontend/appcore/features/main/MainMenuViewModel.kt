package com.codehavenx.alpaca.frontend.appcore.features.main

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationDelegatedEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.UserManager
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the main menu.
 */
class MainMenuViewModel(
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
    private val userManager: UserManager,
) : AlpacaViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(MainMenuUIState(emptyList(), false))
    val uiState: StateFlow<MainMenuUIState> = _uiState

    private val _events = MutableSharedFlow<ApplicationEvent>()
    val events: SharedFlow<ApplicationEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<ApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<ApplicationDelegatedEvent> = _delegatedEvents

    init {
        viewModelScope.launch {
            delegatedEvents.collect {
                logI(TAG, "Delegated event received: $it")
            }
        }
    }

    /**
     * Load the users.
     */
    fun loadUsers() {
        viewModelScope.launch {
            val users = userManager.getUsers().getOrThrow().map { it.toUIModel() }
            _uiState.value = MainMenuUIState(users, false)
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
