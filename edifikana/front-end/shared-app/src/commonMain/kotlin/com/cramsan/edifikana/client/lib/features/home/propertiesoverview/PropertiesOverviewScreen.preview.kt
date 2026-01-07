package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.resources.PropertyIcons
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.PropertyId
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the PropertiesOverview feature screen.
 */
@Preview
@Composable
private fun PropertiesOverviewScreenPreview() {
    AppTheme {
        PropertiesOverviewContent(
            content = PropertiesOverviewUIState(
                isLoading = true,
                propertyList = listOf(
                    PropertyItemUIModel(
                        id = PropertyId("property-1"),
                        name = "Sunny Apartment",
                        address = "123 Main St, Springfield",
                        imageUrl = Image(
                            pointer = painterResource(PropertyIcons.M_DEPA)
                        ),
                    ),
                    PropertyItemUIModel(
                        id = PropertyId("property-2"),
                        name = "Cozy Cottage",
                        address = "456 Oak Ave, Smalltown",
                        imageUrl = null,
                    ),
                ),
            ),
        )
    }
}
