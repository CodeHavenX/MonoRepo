package com.cramsan.framework.sample.shared.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.framework.sample.shared.features.ApplicationNavGraphDestination
import com.cramsan.framework.sample.shared.features.main.assertutil.AssertUtilScreen
import com.cramsan.framework.sample.shared.features.main.configuration.ConfigurationScreen
import com.cramsan.framework.sample.shared.features.main.crashhandler.CrashHandlerScreen
import com.cramsan.framework.sample.shared.features.main.dispatcher.DispatcherScreen
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilScreen
import com.cramsan.framework.sample.shared.features.main.logging.LoggingScreen
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuScreen
import com.cramsan.framework.sample.shared.features.main.metrics.MetricsScreen
import com.cramsan.framework.sample.shared.features.main.preferences.PreferencesScreen
import com.cramsan.framework.sample.shared.features.main.remoteconfig.RemoteConfigScreen
import com.cramsan.framework.sample.shared.features.main.threadutil.ThreadUtilScreen
import com.cramsan.framework.sample.shared.features.main.userevents.UserEventsScreen
import com.cramsan.framework.sample.shared.features.main.welcome.WelcomeDialogScreen
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Main Nav Graph Route.
 */
fun NavGraphBuilder.mainNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = ApplicationNavGraphDestination.MainDestination::class,
        startDestination = MainDestination.MainMenuDestination,
        typeMap = typeMap,
    ) {
        composable(MainDestination.MainMenuDestination::class, typeMap = typeMap) {
            MainMenuScreen()
        }
        composable(MainDestination.HaltUtilDestination::class, typeMap = typeMap) {
            HaltUtilScreen()
        }
        composable(MainDestination.LoggingDestination::class, typeMap = typeMap) {
            LoggingScreen()
        }
        composable(MainDestination.PreferencesDestination::class, typeMap = typeMap) {
            PreferencesScreen()
        }
        composable(MainDestination.ThreadUtilDestination::class, typeMap = typeMap) {
            ThreadUtilScreen()
        }
        composable(MainDestination.AssertUtilDestination::class, typeMap = typeMap) {
            AssertUtilScreen()
        }
        composable(MainDestination.MetricsDestination::class, typeMap = typeMap) {
            MetricsScreen()
        }
        composable(MainDestination.ConfigurationDestination::class, typeMap = typeMap) {
            ConfigurationScreen()
        }
        composable(MainDestination.CrashHandlerDestination::class, typeMap = typeMap) {
            CrashHandlerScreen()
        }
        composable(MainDestination.UserEventsDestination::class, typeMap = typeMap) {
            UserEventsScreen()
        }
        composable(MainDestination.RemoteConfigDestination::class, typeMap = typeMap) {
            RemoteConfigScreen()
        }
        composable(MainDestination.DispatcherDestination::class, typeMap = typeMap) {
            DispatcherScreen()
        }
        dialog(MainDestination.WelcomeDialogDestination::class, typeMap = typeMap) {
            WelcomeDialogScreen()
        }
    }
}
