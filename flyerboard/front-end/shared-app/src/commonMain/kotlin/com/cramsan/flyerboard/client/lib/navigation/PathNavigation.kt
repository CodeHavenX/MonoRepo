package com.cramsan.flyerboard.client.lib.navigation

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
