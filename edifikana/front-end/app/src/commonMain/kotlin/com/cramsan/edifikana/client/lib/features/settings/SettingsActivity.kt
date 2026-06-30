package com.cramsan.edifikana.client.lib.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.settings.general.SettingsScreen
import com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations.MyOrganizationsScreen
import com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail.OrgDetailScreen
import com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership.TransferOwnershipScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Settings Nav Graph Activity.
 */
fun NavGraphBuilder.settingsNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.SettingsNavGraphDestination::class,
        startDestination = SettingsDestination.GeneralSettingsDestination,
        typeMap = typeMap,
    ) {
        composable(SettingsDestination.GeneralSettingsDestination::class) {
            SettingsScreen()
        }
        composable(SettingsDestination.MyOrganizationsDestination::class) {
            MyOrganizationsScreen()
        }
        composable(
            SettingsDestination.OrganizationDetailDestination::class,
            typeMap = typeMap,
        ) {
            OrgDetailScreen(destination = it.toRoute())
        }
        composable(
            SettingsDestination.TransferOwnershipDestination::class,
            typeMap = typeMap,
        ) {
            TransferOwnershipScreen(destination = it.toRoute())
        }
    }
}
