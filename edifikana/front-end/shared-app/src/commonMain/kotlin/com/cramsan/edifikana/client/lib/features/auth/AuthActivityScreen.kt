package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInScreen
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpScreen
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Auth Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.authActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = AuthRoute.SignIn.route,
    ) {
        AuthRoute.entries.forEach {
            when (it) {
                AuthRoute.SignIn -> composable(it.route) {
                    SignInScreen()
                }
                AuthRoute.SignUp -> composable(it.route) {
                    SignUpScreen()
                }
            }
        }
    }
}
