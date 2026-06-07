package com.cramsan.edifikana.client.lib.features.home.propertyhome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.home.eventlog.EventLogScreen
import com.cramsan.edifikana.client.lib.features.home.gotoorganization.GoToOrganizationScreen
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.home_screen_property_dropdown_description
import edifikana_lib.home_screen_property_dropdown_selected_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Property home screen.
 *
 * Displays a property selector and the event log for the selected property.
 * Used as the content for the Properties shell tab.
 */
@Composable
fun PropertyHomeScreen(
    onNavigateToOrganization: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PropertyHomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadContent()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Reserved for future viewModel calls
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PropertyHomeEvent.Noop -> Unit
        }
    }

    PropertyHomeScreenContent(
        uiState = uiState,
        onPropertySelected = { viewModel.selectProperty(it) },
        onNavigateToOrganization = onNavigateToOrganization,
        modifier = modifier,
    )
}

/**
 * Content of the Property Home screen.
 */
@Composable
internal fun PropertyHomeScreenContent(
    uiState: PropertyHomeUIModel,
    onPropertySelected: (PropertyId) -> Unit,
    onNavigateToOrganization: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.availableProperties.isNotEmpty()) {
            PropertyDropDown(
                label = uiState.label,
                list = uiState.availableProperties,
                onPropertySelected = onPropertySelected,
                modifier = Modifier.padding(horizontal = Padding.MEDIUM, vertical = Padding.X_SMALL),
            )
        }
        HomeContent(
            uIModel = uiState,
            onNavigateToOrganization = onNavigateToOrganization,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PropertyDropDown(
    label: String,
    list: List<PropertyUiModel>,
    modifier: Modifier = Modifier,
    onPropertySelected: (PropertyId) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier,
    ) {
        AnimatedContent(label) {
            Row(
                modifier =
                Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                    ) {
                        expanded = !expanded
                    }.padding(Padding.X_SMALL),
            ) {
                Text(it)
                Spacer(Modifier.width(Padding.X_SMALL))
                Icon(
                    Icons.Default.Apartment,
                    contentDescription = stringResource(Res.string.home_screen_property_dropdown_description),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            list.forEach { uiModel ->
                DropdownMenuItem(
                    text = { Text(uiModel.name) },
                    onClick = {
                        onPropertySelected(uiModel.propertyId)
                        expanded = false
                    },
                    trailingIcon = {
                        if (uiModel.selected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription =
                                stringResource(
                                    Res.string.home_screen_property_dropdown_selected_description,
                                ),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uIModel: PropertyHomeUIModel,
    onNavigateToOrganization: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(uIModel, modifier = modifier) {
        val selectedTab = it.selectedTab
        val propertyId = it.propertyId
        when (selectedTab) {
            Tabs.None -> {
                // No content
            }

            Tabs.GoToOrganization -> {
                GoToOrganizationScreen(
                    onNavigateToOrganization = onNavigateToOrganization,
                )
            }

            Tabs.EventLog -> {
                if (propertyId != null) {
                    EventLogScreen(
                        propertyId = propertyId,
                    )
                }
            }
        }
    }
}
