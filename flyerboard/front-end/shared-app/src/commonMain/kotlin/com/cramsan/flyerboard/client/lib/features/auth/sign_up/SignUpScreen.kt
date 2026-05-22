package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size
import flyerboard_lib.Res
import flyerboard_lib.sign_up_screen_button_sign_up
import flyerboard_lib.sign_up_screen_email_placeholder
import flyerboard_lib.sign_up_screen_have_account
import flyerboard_lib.sign_up_screen_label_confirm_password
import flyerboard_lib.sign_up_screen_label_email
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
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier =
                Modifier
                    .widthIn(max = Size.COLUMN_MAX_WIDTH)
                    .padding(Padding.LARGE),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = Padding.X_SMALL),
                colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Padding.XX_SMALL)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Column(
                    modifier = Modifier.padding(Padding.LARGE),
                    verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(Res.string.sign_up_screen_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(Res.string.sign_up_screen_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(Padding.X_SMALL))
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
                                        imageVector = if (passwordVisible)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility,
                                        contentDescription = null,
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
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
                                        imageVector = if (confirmPasswordVisible)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility,
                                        contentDescription = null,
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
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
            LoadingAnimationOverlay(uiState.isLoading)
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
