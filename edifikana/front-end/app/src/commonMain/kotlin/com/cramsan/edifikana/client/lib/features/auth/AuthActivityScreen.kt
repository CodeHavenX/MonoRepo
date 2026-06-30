package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.auth.invitationaccept.InvitationAcceptScreen
import com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg.CreateNewOrgScreen
import com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg.SelectOrgScreen
import com.cramsan.edifikana.client.lib.features.auth.passwordreset.PasswordResetScreen
import com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation.PasswordResetConfirmationScreen
import com.cramsan.edifikana.client.lib.features.auth.setnewpassword.SetNewPasswordScreen
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
            SignUpScreen(
                destination = it.toRoute(),
            )
        }
        composable(
            AuthDestination.ValidationDestination::class,
            typeMap = typeMap,
        ) { backStackEntry ->
            OtpValidationScreen(
                destination = backStackEntry.toRoute(),
            )
        }
        composable(
            AuthDestination.SelectOrgDestination::class,
            typeMap = typeMap,
        ) {
            SelectOrgScreen()
        }
        composable(
            AuthDestination.CreateNewOrgDestination::class,
            typeMap = typeMap,
        ) {
            CreateNewOrgScreen()
        }
        composable(
            AuthDestination.PasswordResetDestination::class,
            typeMap = typeMap,
        ) {
            PasswordResetScreen(destination = it.toRoute())
        }
        composable(
            AuthDestination.PasswordResetConfirmationDestination::class,
            typeMap = typeMap,
        ) {
            PasswordResetConfirmationScreen(destination = it.toRoute())
        }
        composable(
            AuthDestination.SetNewPasswordDestination::class,
            typeMap = typeMap,
        ) {
            SetNewPasswordScreen(destination = it.toRoute())
        }
        composable(
            AuthDestination.InvitationAcceptDestination::class,
            typeMap = typeMap,
        ) {
            InvitationAcceptScreen(destination = it.toRoute())
        }
    }
}
