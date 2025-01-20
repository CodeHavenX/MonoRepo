@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package ${PACKAGE_NAME}

/**
 * Routes in the ${NAME} activity.
 */
enum class ${NAME}ActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Example(route = "example"),
    ;
}

/**
 * Destinations in the ${NAME} activity.
 */
sealed class ${NAME}RouteDestination(
    @RouteSafePath
    val path: String,
) {

    /**
     * An example class representing navigating to a screen within the ${NAME} activity.
     */
    data object Example${NAME}Destination : ${NAME}RouteDestination(
        ${NAME}ActivityRoute.Example.route,
    )
}
