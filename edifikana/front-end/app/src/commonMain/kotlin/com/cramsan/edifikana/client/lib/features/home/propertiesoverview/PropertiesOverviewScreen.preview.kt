package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the PropertiesOverview feature screen.
 */
@ScreenPreviews
@Composable
private fun PropertiesOverviewScreenPreview() {
    AppTheme {
        PropertiesOverviewContent(
            content =
            PropertiesOverviewUIState(
                isLoading = true,
                propertyList =
                listOf(
                    PropertyItemUIModel(
                        id = PropertyId("property-1"),
                        name = "Sunny Apartment",
                        address = "123 Main St, Springfield",
                        imageUrl = "drawable:M_DEPA",
                    ),
                    PropertyItemUIModel(
                        id = PropertyId("property-2"),
                        name = "Cozy Cottage",
                        address = "456 Oak Ave, Smalltown",
                        imageUrl = "drawable:CASA",
                    ),
                ),
            ),
        )
    }
}

@ScreenPreviews
@Composable
private fun PropertiesOverviewScreenPreview_Empty_ES() {
    AppTheme {
        PropertiesOverviewContent(
            content =
            PropertiesOverviewUIState(
                isLoading = false,
                propertyList = emptyList(),
            ),
        )
    }
}

@ScreenPreviews
@Composable
private fun PropertiesOverviewScreenPreview_ES() {
    AppTheme {
        PropertiesOverviewContent(
            content =
            PropertiesOverviewUIState(
                isLoading = false,
                propertyList =
                listOf(
                    PropertyItemUIModel(
                        id = PropertyId("property-1"),
                        name = "Departamento soleado",
                        address = "Av. Principal 123, Lima",
                        imageUrl = "drawable:M_DEPA",
                    ),
                    PropertyItemUIModel(
                        id = PropertyId("property-2"),
                        name = "Casa acogedora",
                        address = "Jr. Los Robles 456, Miraflores",
                        imageUrl = "drawable:CASA",
                    ),
                ),
            ),
        )
    }
}
