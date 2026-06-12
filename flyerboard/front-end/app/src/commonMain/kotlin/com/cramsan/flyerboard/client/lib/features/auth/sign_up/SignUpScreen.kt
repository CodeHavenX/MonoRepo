package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.cramsan.flyerboard.client.ui.components.FlyerBoardFormCard
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.sign_up_screen_button_sign_up
import flyerboard_lib.sign_up_screen_email_placeholder
import flyerboard_lib.sign_up_screen_have_account
import flyerboard_lib.sign_up_screen_label_confirm_password
import flyerboard_lib.sign_up_screen_label_email
import flyerboard_lib.sign_up_screen_label_first_name
import flyerboard_lib.sign_up_screen_label_last_name
import flyerboard_lib.sign_up_screen_label_password
import flyerboard_lib.sign_up_screen_sign_in_link
import flyerboard_lib.sign_up_screen_subtitle
import flyerboard_lib.sign_up_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign Up screen.
 */
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SignUpEvent.Noop -> Unit
        }
    }

    SignUpContent(
        uiState = uiState,
        modifier = modifier,
        onFirstNameChanged = { viewModel.onFirstNameChanged(it) },
        onLastNameChanged = { viewModel.onLastNameChanged(it) },
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onConfirmPasswordChanged = { viewModel.onConfirmPasswordChanged(it) },
        onSignUpClicked = { viewModel.signUp() },
        onSignInClicked = { viewModel.navigateToSignIn() },
    )
}

/**
 * Content of the Sign Up screen.
 */
@Composable
internal fun SignUpContent(
    uiState: SignUpUIState,
    modifier: Modifier = Modifier,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    FlyerBoardFormCard(
        title = stringResource(Res.string.sign_up_screen_title),
        subtitle = stringResource(Res.string.sign_up_screen_subtitle),
        modifier = modifier,
        isLoading = uiState.isLoading,
    ) {
        LabeledField(label = stringResource(Res.string.sign_up_screen_label_first_name)) {
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = onFirstNameChanged,
                shape = CircleShape,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LabeledField(label = stringResource(Res.string.sign_up_screen_label_last_name)) {
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = onLastNameChanged,
                shape = CircleShape,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LabeledField(label = stringResource(Res.string.sign_up_screen_label_email)) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                placeholder = { Text(stringResource(Res.string.sign_up_screen_email_placeholder)) },
                shape = CircleShape,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LabeledField(label = stringResource(Res.string.sign_up_screen_label_password)) {
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder = { Text(stringResource(Res.string.sign_up_screen_label_password)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector =
                            if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = null,
                        )
                    }
                },
                visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                shape = CircleShape,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LabeledField(label = stringResource(Res.string.sign_up_screen_label_confirm_password)) {
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                placeholder = { Text(stringResource(Res.string.sign_up_screen_label_confirm_password)) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector =
                            if (confirmPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = null,
                        )
                    }
                },
                visualTransformation =
                if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                shape = CircleShape,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Button(
            onClick = onSignUpClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
        ) {
            Text(stringResource(Res.string.sign_up_screen_button_sign_up))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.sign_up_screen_have_account),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onSignInClicked) {
                Text(
                    text = stringResource(Res.string.sign_up_screen_sign_in_link),
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}
