package com.cramsan.edifikana.client.lib.features.home.gotoorganization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.go_to_org_screen_get_started_button
import edifikana_lib.go_to_org_screen_no_properties_message
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * GoToOrganization screen.
 *
 * Displayed when the current user has no properties configured. Provides a prompt to navigate
 * to the organization management view to create or join a property.
 */
@Composable
fun GoToOrganizationScreen(
    onNavigateToOrganization: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GoToOrganizationViewModel = koinViewModel(),
) {
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Reserved for future viewModel calls
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Reserved for future viewModel calls
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            GoToOrganizationEvent.Noop -> Unit
        }
    }

    GoToOrganizationContent(
        modifier = modifier,
        onNavigateToOrganizationsClicked = onNavigateToOrganization,
    )
}

/**
 * Content of the GoToOrganization screen.
 */
@Composable
internal fun GoToOrganizationContent(
    modifier: Modifier = Modifier,
    onNavigateToOrganizationsClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ScreenLayout(
            sectionContent = { sectionModifier ->
                Text(
                    stringResource(Res.string.go_to_org_screen_no_properties_message),
                    modifier = sectionModifier,
                )
                Button(
                    onClick = onNavigateToOrganizationsClicked,
                    modifier = sectionModifier,
                ) {
                    Text(stringResource(Res.string.go_to_org_screen_get_started_button))
                }
            },
        )
    }
}
