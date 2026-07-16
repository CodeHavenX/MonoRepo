package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.auth.postAuthenticationDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.models.Theme
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.loginvalidation.validateEmail
import com.cramsan.ui.components.themetoggle.SelectedTheme
import edifikana_lib.Res
import edifikana_lib.error_message_invalid_credentials
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * Sign in ViewModel.
 */
@FrontendViewModel
class SignInViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
    private val organizationManager: OrganizationManager,
    private val stringProvider: StringProvider,
    private val preferencesManager: PreferencesManager,
) : BaseViewModel<SignInEvent, SignInUIState>(dependencies, SignInUIState.Initial, TAG) {
    /**
     * Initialize the page.
     */
    fun initializePage(inviteId: InviteId?) {
        logI(TAG, "SignInViewModel initialized")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(inviteId = inviteId) }
        }
    }

    /**
     * Called when the username value changes.
     */
    fun changeUsernameValue(username: String) {
        viewModelCoroutineScope.launch {
            logD(TAG, "onUsernameValueChange called")
            updateUiState {
                it.copy(
                    email = username,
                )
            }
        }
    }

    /**
     * Called when the password value changes.
     */
    fun changePasswordValue(password: String) {
        viewModelCoroutineScope.launch {
            logD(TAG, "onPasswordValueChange called")
            updateUiState {
                it.copy(
                    password = password,
                )
            }
        }
    }

    /**
     * Call this function to continue signing in with a password.
     * We will show the password field and the button will change
     * to enable the sign-in process.
     */
    fun continueWithPassword() {
        logI(TAG, "continue sign in with a password.")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(showPassword = true) }
        }
    }

    /**
     * Call this function to sign in the user.
     */
    fun signInWithPassword() {
        logI(TAG, "signIn called")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val email = uiState.value.email.trim()
            val password = uiState.value.password
            auth
                .signInWithPassword(
                    email = email,
                    password = password,
                ).onFailure { error ->
                    val message = getErrorMessage(error)
                    updateUiState {
                        it.copy(
                            errorMessages = listOf(message),
                            isLoading = false,
                        )
                    }
                    return@launch
                }

            val inviteId = uiState.value.inviteId
            val navEvent = postAuthenticationDestination(organizationManager, inviteId = inviteId)
            emitWindowEvent(navEvent)
        }
    }

    /**
     * Sign in with OTP - navigates to our otp validation screen, carrying the email with it.
     */
    fun signInWithOtp() {
        logI(TAG, "signIn with OTP called")
        viewModelCoroutineScope.launch {
            val email = uiState.value.email.trim()
            val inviteId = uiState.value.inviteId
            if (!checkEmailValid(email)) {
                return@launch
            }
            val result = auth.checkUserExists(email)
            result.onFailure {
                updateUiState {
                    it.copy(
                        errorMessages = listOf(stringProvider.getString(Res.string.error_message_unexpected_error)),
                    )
                }
                return@launch
            }
            val registeredUser = result.getOrNull() ?: false
            if (registeredUser) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        AuthDestination.ValidationDestination(
                            email,
                            accountCreationFlow = false,
                            inviteId = inviteId,
                        ),
                    ),
                )
            } else {
                emitWindowEvent(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        AuthDestination.SignUpDestination(
                            userEmail = email,
                            inviteId = inviteId,
                        ),
                    ),
                )
            }
        }
    }

    /**
     * Navigate to the password reset page.
     */
    fun navigateToPasswordReset() {
        viewModelCoroutineScope.launch {
            val email = uiState.value.email.trim()
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.PasswordResetDestination(prefillEmail = email),
                ),
            )
        }
    }

    /**
     * Navigate to the signUp page.
     */
    fun navigateToSignUpPage() {
        viewModelCoroutineScope.launch {
            val email = uiState.value.email.trim()
            val inviteId = uiState.value.inviteId
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignUpDestination(
                        userEmail = email,
                        inviteId = inviteId,
                    ),
                ),
            )
        }
    }

    /**
     * Navigate to the debug page.
     */
    fun navigateToDebugPage() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.DebugNavGraphDestination),
            )
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"
    }

    /**
     * Check if the email is valid before proceeding with the sign in option
     */
    private suspend fun checkEmailValid(email: String): Boolean {
        val errorMessages = validateEmail(email)
        if (errorMessages.isNotEmpty()) {
            logD(TAG, "email field found to be invalid.")
            updateUiState {
                it.copy(
                    errorMessages = errorMessages,
                )
            }
            return false
        }
        updateUiState {
            it.copy(
                errorMessages = emptyList(),
            )
        }
        return true
    }

    /**
     * Get the custom client error message based on the exception type.
     */
    private suspend fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ClientRequestExceptions.UnauthorizedException -> {
                stringProvider.getString(Res.string.error_message_invalid_credentials)
            }

            else -> {
                stringProvider.getString(Res.string.error_message_unexpected_error)
            }
        }
    }

    /**
     * Change the selected theme preference.
     */
    fun changeSelectedTheme(theme: SelectedTheme) {
        viewModelCoroutineScope.launch {
            val themeToSave =
                when (theme) {
                    SelectedTheme.LIGHT -> Theme.LIGHT
                    SelectedTheme.DARK -> Theme.DARK
                    SelectedTheme.SYSTEM_DEFAULT -> Theme.SYSTEM_DEFAULT
                }
            preferencesManager
                .updatePreference(
                    EdifikanaSettingKey.SelectedTheme,
                    themeToSave.name,
                ).getOrThrow()
        }
    }
}
