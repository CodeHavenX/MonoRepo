package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInScreen
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpScreen
import com.cramsan.edifikana.client.lib.features.auth.validation.OtpValidationScreen
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph

/**
 * Auth Activity Route.
 */
fun NavGraphBuilder.authActivityNavigation() {
    navigationGraph(
        graphDestination = ActivityRouteDestination.AuthRouteDestination::class,
        startDestination = AuthRouteDestination.SignInDestination,
    ) {
        composable(AuthRouteDestination.SignInDestination::class) {
            SignInScreen()
        }
        composable(AuthRouteDestination.SignUpDestination::class) {
            SignUpScreen()
        }
        composable(AuthRouteDestination.ValidationDestination::class) { backStackEntry ->
            OtpValidationScreen(
                destination = backStackEntry.toRoute()
            )
        }
    }
}
