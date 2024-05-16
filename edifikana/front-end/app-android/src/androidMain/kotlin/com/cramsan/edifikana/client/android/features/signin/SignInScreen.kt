package com.cramsan.edifikana.client.android.features.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.lib.signInString

@Composable
fun SignInScreen(
    uiState: SignInUIState,
    onDismissRequest: () -> Unit,
    signSingInClicked: () -> Unit,
    infoButtonClicked: () -> Unit,
    onCodeSubmitClicked: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = signSingInClicked) {
            Text(signInString)
        }

        Icon(
            Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .clickable { infoButtonClicked() },
        )

        AnimatedVisibility(visible = uiState.showAccessCodeDialog) {
            Dialog(onDismissRequest = { onDismissRequest() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    var inputCode by remember { mutableStateOf("") }
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.text_input_activation_code),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        TextField(
                            value = inputCode,
                            onValueChange = { inputCode = it },
                            label = { Text(stringResource(R.string.text_code)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(onClick = { onCodeSubmitClicked(inputCode) }) {
                            Text(stringResource(R.string.text_accept))
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun SignInScreenPreview() {
    SignInScreen(
        uiState = SignInUIState(showAccessCodeDialog = true),
        onDismissRequest = {},
        signSingInClicked = {},
        infoButtonClicked = {},
        onCodeSubmitClicked = {},
    )
}
