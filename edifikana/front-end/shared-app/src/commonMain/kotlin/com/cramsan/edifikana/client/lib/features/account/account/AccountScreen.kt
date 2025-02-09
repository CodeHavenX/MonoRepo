package com.cramsan.edifikana.client.lib.features.account.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Account screen.
 */
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(AccountEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            AccountEvent.Noop -> Unit
            is AccountEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(event.edifikanaApplicationEvent)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "Account",
                onCloseClicked = { viewModel.navigateBack() },
            )
        },
    ) { innerPadding ->
        AccountContent(
            uiState.content,
            modifier = Modifier.padding(innerPadding)
        ) {
            viewModel.signOut()
        }
    }
}

@Composable
internal fun AccountContent(
    content: AccountUIModel,
    modifier: Modifier = Modifier,
    onSignOutClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text("Account Screen: ${content.name}")
        Button(onSignOutClicked) {
            Text("Sign Out")
        }
    }
}
