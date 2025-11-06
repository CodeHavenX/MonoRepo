package ${PACKAGE_NAME}.${Package_Name}

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * ${Feature_Name} Nav Graph Activity.
 */
 // TODO: Register this nav graph within the root nav host. It is usually called WindowNavigationHost
fun NavGraphBuilder.${Feature_Name.toLowerCase()}NavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        // TODO: Register this NavGraphNavigation with the application wide NavGraphDestination
        graphDestination = ApplicationNavGraphDestination.${Feature_Name}NavGraphDestination::class,
        // TODO: Update the start destination to the correct one from ${Feature_Name}Destination
        startDestination = ${Feature_Name}Destination.Feature1Destination, 
        typeMap = typeMap,
    ) {
        // TODO: Map your destinations to their respective screen
        
        // Here is how to launch a simple screen that does not require any arguments
        // composable(${Feature_Name}Destination.Feature1Destination::class) {
        //    Feature1Screen()
        // }
 
        // Here is how to map a screen that takes in a destination with an argument
        // composable(
        //     ${Feature_Name}Destination.Feature2Destination::class,
        //     typeMap = typeMap,
        // ) { backStackEntry ->
        //     Feature2Screen(
        //         id = backStackEntry.toRoute<${Feature_Name}Destination.Feature2Destination>().id,
        //     )
        // }
    }
}
