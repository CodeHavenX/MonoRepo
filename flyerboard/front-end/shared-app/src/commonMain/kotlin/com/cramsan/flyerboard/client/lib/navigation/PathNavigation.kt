package com.cramsan.flyerboard.client.lib.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.cramsan.flyerboard.client.lib.features.auth.AuthDestination
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Converts a canonical URL path into the matching typed navigation [Destination].
 * This is the authoritative path → destination mapping used when the app launches
 * via a direct URL or a browser back/forward event.
 */
fun pathToDestination(path: String): Destination =
    when {
        path.startsWith("/flyer/") -> {
            MainDestination.FlyerDetailDestination(path.removePrefix("/flyer/"))
        }

        path.startsWith("/my-flyers/edit/") -> {
            MainDestination.FlyerEditDestination(path.removePrefix("/my-flyers/edit/"))
        }

        path == "/archive" -> {
            MainDestination.ArchiveDestination
        }

        path == "/my-flyers" -> {
            MainDestination.MyFlyersDestination
        }

        path == "/moderation" -> {
            MainDestination.ModerationQueueDestination
        }

        path == "/sign-in" -> {
            AuthDestination.SignInDestination
        }

        path == "/sign-up" -> {
            AuthDestination.SignUpDestination
        }

        else -> {
            MainDestination.FlyerListDestination
        }
    }

/**
 * Converts the current [NavBackStackEntry] to a canonical URL path for FlyerBoard.
 * Returns null for destinations that should not update the browser URL (nav-graph
 * containers or the splash screen).
 */
fun flyerBoardEntryToPath(entry: NavBackStackEntry): String? {
    val destination = entry.destination
    return when {
        destination.hasRoute<MainDestination.FlyerListDestination>() -> {
            "/"
        }

        destination.hasRoute<MainDestination.FlyerDetailDestination>() -> {
            "/flyer/${entry.toRoute<MainDestination.FlyerDetailDestination>().flyerId}"
        }

        destination.hasRoute<MainDestination.ArchiveDestination>() -> {
            "/archive"
        }

        destination.hasRoute<MainDestination.MyFlyersDestination>() -> {
            "/my-flyers"
        }

        destination.hasRoute<MainDestination.FlyerEditDestination>() -> {
            "/my-flyers/edit/${entry.toRoute<MainDestination.FlyerEditDestination>().flyerId}"
        }

        destination.hasRoute<MainDestination.ModerationQueueDestination>() -> {
            "/moderation"
        }

        destination.hasRoute<AuthDestination.SignInDestination>() -> {
            "/sign-in"
        }

        destination.hasRoute<AuthDestination.SignUpDestination>() -> {
            "/sign-up"
        }

        else -> {
            null
        }
    }
}
