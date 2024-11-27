package com.cramsan.edifikana.client.lib.features.root.account.account

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.account.AccountActivityViewModel
import org.koin.compose.koinInject

/**
 * Account screen.
 */
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = koinInject(),
    accountActivityViewModel: AccountActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(AccountEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            AccountEvent.Noop -> Unit
            is AccountEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(event.edifikanaApplicationEvent)
            }
            is AccountEvent.TriggerAccountActivityEvent -> {
                accountActivityViewModel.executeEvent(event.accountActivityEvent)
            }
        }
    }

    AccountContent(
        uiState.content,
    ) {
        viewModel.signOut()
    }
}

@Composable
internal fun AccountContent(content: AccountUIModel, onSignOutClicked: () -> Unit) {
    // This is a placeholder. The actual content will be more complex.
    Text("Account Screen: ${content.name}")
    Button(onSignOutClicked) {
        Text("Sign Out")
    }
}
