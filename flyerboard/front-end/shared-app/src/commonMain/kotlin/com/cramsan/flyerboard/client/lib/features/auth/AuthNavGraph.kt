package com.cramsan.flyerboard.client.lib.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.flyerboard.client.lib.features.auth.sign_in.SignInScreen
import com.cramsan.flyerboard.client.lib.features.auth.sign_up.SignUpScreen
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Auth nav graph — wires sign-in and sign-up screens under [AuthNavGraphDestination].
 */
fun NavGraphBuilder.authNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = FlyerBoardWindowNavGraphDestination.AuthNavGraphDestination::class,
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
    }
}
