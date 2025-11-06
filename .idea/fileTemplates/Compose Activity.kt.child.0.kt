package ${PACKAGE_NAME}.${Package_Name}

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the ${Feature_Name} graph.
 */
sealed class ${Feature_Name}Destination : Destination {
     // TODO: Add here your first destination

    // If you need to navigate to a screen without neededing to pass arguments, use a data object directly.
    //@Serializable
    //data object Feature1Destination : ${Feature_Name}Destination()
    
    // For cases when you need to pass arguments, use a data class.
    //@Serializable
    //data class(
    // val id: String,
    //) Feature2Destination : ${Feature_Name}Destination()
}
