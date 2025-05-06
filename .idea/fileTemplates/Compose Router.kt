package ${PACKAGE_NAME}

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Account Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.${NAME}RouterNavigation(
    route: String,
) {
    navigation(
        route = route,
        // TODO: Define a start destination for this router.
        startDestination = TODO("Define a start destination for this router"),
    ) {
        ${NAME}Route.entries.forEach {
            when (it) {
                // TODO: Add any new routes here
                /*
                SomeRoute.FirstScreen -> composable(it.route) {
                    FirstScreen()
                }
                */
            }
        }
    }
}