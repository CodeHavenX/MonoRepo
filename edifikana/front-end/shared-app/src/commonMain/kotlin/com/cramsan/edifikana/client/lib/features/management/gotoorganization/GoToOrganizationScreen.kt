package com.cramsan.edifikana.client.lib.features.management.gotoorganization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementViewModel
import com.cramsan.edifikana.client.lib.features.management.drawer.SelectableDrawerItem
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * GoToOrganization screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun GoToOrganizationScreen(
    modifier: Modifier = Modifier,
    managementViewModel: ManagementViewModel = koinViewModel(),
    viewModel: GoToOrganizationViewModel = koinViewModel(),
) {
    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                GoToOrganizationEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    GoToOrganizationContent(
        modifier,
    ) {
        managementViewModel.selectDrawerItem(SelectableDrawerItem.Organization)
    }
}

/**
 * Content of the GoToOrganization.
 */
@Composable
internal fun GoToOrganizationContent(
    modifier: Modifier = Modifier,
    onNavigateToOrganizationsClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ScreenLayout(
            sectionContent = { sectionModifier ->
                Text(
                    "You don't have any properties yet. Create your first property to get started!",
                    modifier = sectionModifier,
                )
                Button(
                    onClick = onNavigateToOrganizationsClicked,
                    modifier = sectionModifier,
                ) {
                    Text("Get Started!")
                }
            },
        )
    }
}
