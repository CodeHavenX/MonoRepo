package com.codehavenx.alpaca.frontend.appcore.features.signin

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.managers.AuthenticationManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Events for the Sign In screen.
 */
class SignInViewModel(
    private val authManager: AuthenticationManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        SignInUIState(
            content = SignInUIModel("", "", error = false),
            isLoading = true,
        )
    )
    val uiState: StateFlow<SignInUIState> = _uiState

    private val _event = MutableSharedFlow<SignInEvent>()
    val event: SharedFlow<SignInEvent> = _event

    /**
     * Start the flow.
     */
    fun startFlow() {
        viewModelScope.launch {
            if (authManager.isUserSignedIn().getOrNull() == true) {
                _event.emit(SignInEvent.TriggerApplicationEvent(ApplicationEvent.SignInStatusChange(isSignedIn = true)))
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Handle the username change.
     */
    fun onUsernameChanged(it: String) {
        _uiState.value = _uiState.value.copy(
            content = _uiState.value.content.copy(username = it)
        )
    }

    /**
     * Handle the password change.
     */
    fun onPasswordChanged(it: String) {
        _uiState.value = _uiState.value.copy(
            content = _uiState.value.content.copy(password = it)
        )
    }

    /**
     * Handle the sign in button click.
     */
    fun onSignInClicked() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                content = _uiState.value.content.copy(error = false)
            )
            val username = _uiState.value.content.username
            val password = _uiState.value.content.password
            if (authManager.signIn(username, password).isSuccess) {
                _event.emit(SignInEvent.TriggerApplicationEvent(ApplicationEvent.SignInStatusChange(isSignedIn = true)))
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    content = _uiState.value.content.copy(error = true)
                )
            }
        }
    }
}
