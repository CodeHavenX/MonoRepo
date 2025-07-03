package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.alpacaIcon
import edifikana_lib.otp_validation_screen_text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Validation screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun OtpValidationScreen(
    destination: AuthRouteDestination.ValidationDestination,
    viewModel: OtpValidationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenScope = rememberCoroutineScope()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializeOTPValidationScreen(destination.userEmail)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(screenScope) {
        screenScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    OtpValidationEvent.Noop -> Unit
                }
            }
        }
    }

    // Render the screen
    OtpValidationContent(
        uiState = uiState,
        onLoginClicked = {
            viewModel.signInWithOtp()
        },
        onBackClicked = {
            viewModel.navigateBack()
        },
        onOtpFieldFocused = {
            viewModel.onOtpFieldFocused(it)
        },
        onEnterOtpValue = { value, index ->
            viewModel.onEnterOtpValue(value, index)
        },
        onKeyboardBack = {
            viewModel.onKeyboardBack()
        }
    )
}

/**
 * Content of the OTP Validation screen.
 */
@Composable
internal fun OtpValidationContent(
    uiState: OtpValidationUIState,
    modifier: Modifier = Modifier,
    onLoginClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onOtpFieldFocused: (Int) -> Unit,
    onEnterOtpValue: (Int?, Int) -> Unit,
    onKeyboardBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                onNavigationIconSelected = onBackClicked,
            )
        },
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    // Image above the text
                    Image(
                        painter = painterResource(Res.drawable.alpacaIcon),
                        contentDescription = "Validation Image",
                        modifier = sectionModifier.size(
                            width = 150.dp,
                            height = 150.dp
                        ),
                    )
                    // Display text message
                    Text(
                        text = stringResource(Res.string.otp_validation_screen_text),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = sectionModifier
                            .wrapContentWidth(),
                    )

                    // Error message
                    AnimatedContent(
                        uiState.errorMessage,
                    ) {
                        val showErrorMessage = it.isNullOrBlank().not()
                        if (showErrorMessage) {
                            // Render the error message
                            Text(
                                it.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = sectionModifier
                                    .wrapContentWidth(),
                            )
                        }
                    }
                    // OTP input fields
                    val focusRequesters = remember {
                        List(OTP_CODE_SIZE) { FocusRequester() }
                    }
                    val focusManager = LocalFocusManager.current
                    val keyboardManager = LocalSoftwareKeyboardController.current

                    LaunchedEffect(uiState.focusedIndex) {
                        uiState.focusedIndex?.let { index ->
                            focusRequesters.getOrNull(index)?.requestFocus()
                        }
                    }

                    LaunchedEffect(uiState.otpCode, keyboardManager) {
                        val allNumbersEntered = uiState.otpCode.none { it == null }
                        if (allNumbersEntered) {
                            focusRequesters.forEach {
                                it.freeFocus()
                            }
                            focusManager.clearFocus()
                            keyboardManager?.hide()
                        }
                    }
                    // generate the input fields
                    OtpSection(
                        uiState,
                        focusRequesters,
                        onOtpFieldFocused,
                        onEnterOtpValue,
                        onKeyboardBack
                    )

                    // Submit button
                    ElevatedButton(
                        onClick = {
                            onLoginClicked()
                        },
                        enabled = uiState.otpCode.none { it == null },
                        modifier = sectionModifier
                            .padding(top = 16.dp)
                            .wrapContentWidth()
                    ) {
                        Text("Login")
                    }
                }
            )
        }
    }
}

/**
 * OTP field input box. Creates the field to enter an OTP code
 */
@Suppress("UnusedParameter")
@Composable
fun OtpInputField(
    value: Int?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onValueChanged: (Int?) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // State to hold the text field value and focus state
    var text by remember {
        mutableStateOf(
            TextFieldValue(
                text = value?.toString().orEmpty(),
                selection = TextRange(
                    index = if (value != null) 1 else 0
                )
            )
        )
    }
    // Handle focus changes
    var isFocused by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .border(
                width = 8.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp),
            )
            .size(width = 45.dp, height = 65.dp)
            .shadow(3.dp, RoundedCornerShape(20.dp))
            .background(Color.LightGray, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                val newValue = newText.text
                if (newValue.length <= 1) {
                    onValueChanged(newValue.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 36.sp,
                color = Color.Black,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            modifier = Modifier
                .padding(10.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                }
                .onKeyEvent { event ->
                    val didPressDelete = event.key == Key.Delete
                    if (didPressDelete && value == null) {
                        onKeyboardBack()
                    }
                    false
                },
            decorationBox = { innerBox ->
                innerBox()
                if (!isFocused && value == null) {
                    Text(
                        text = "",
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }
            }
        )
    }
}

/**
 * Generates the OTP input section for the six digit code we expect for the sign in
 */
@Composable
fun OtpSection(
    uistate: OtpValidationUIState,
    focusRequesters: List<FocusRequester>,
    onOtpFieldFocused: (Int) -> Unit,
    onEnterOtpValue: (Int?, Int) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        uistate.otpCode.forEachIndexed { index, value ->
            OtpInputField(
                value = value,
                focusRequester = focusRequesters[index],
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        onOtpFieldFocused(index)
                    }
                },
                onValueChanged = { newValue ->
                    onEnterOtpValue(newValue, index)
                },
                onKeyboardBack = {
                    onKeyboardBack()
                },
                modifier = Modifier
            )
        }
    }
}

private const val OTP_CODE_SIZE = 6
