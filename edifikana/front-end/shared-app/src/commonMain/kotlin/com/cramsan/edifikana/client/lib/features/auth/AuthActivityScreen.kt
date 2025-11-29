package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInScreen
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpScreen
import com.cramsan.edifikana.client.lib.features.auth.validation.OtpValidationScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Auth Nav Graph Route.
 */
fun NavGraphBuilder.authNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.AuthNavGraphDestination::class,
        startDestination = AuthDestination.SignInDestination,
        typeMap = typeMap,
    ) {
        composable(
            AuthDestination.SignInDestination::class,
            typeMap = typeMap,
        ) {
            SignInScreen()
        }
        composable(
            AuthDestination.SignUpDestination::class,
            typeMap = typeMap,
        ) {
            SignUpScreen()
        }
        composable(
            AuthDestination.ValidationDestination::class,
            typeMap = typeMap,
        ) { backStackEntry ->
            OtpValidationScreen(
                destination = backStackEntry.toRoute()
            )
        }
    }
}
