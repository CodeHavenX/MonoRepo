@file:OptIn(RouteSafePath::class)

package ${PACKAGE_NAME}

import com.cramsan.edifikana.client.lib.features.Destination
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes for the ${NAME} router.
 * These map 1:1 with a feature/screen to be rendered.
 */
enum class ${NAME}Route(
    @RouteSafePath
    val route: String,
) {
    // TODO: Create your first route here
    // MyScreen(route = "myscreen")
    ;
}

/**
 * Destinations in the ${NAME} ruter.
 * These are used to transition to a new [${NAME}Route].
 */
sealed class AccountRouteDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {
    // TODO: Add a destination
}