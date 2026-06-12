package com.cramsan.framework.webroute.ksp

import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateWebRoutesSourceTest {
    @Test
    fun `generates an exhaustive routing object for each entry`() {
        val source =
            generateWebRoutesSource(
                packageName = "com.example",
                rootName = "MainDestination",
                entries =
                listOf(
                    RouteEntry("FlyerListDestination", "/"),
                    RouteEntry("FlyerDetailDestination", "/flyer"),
                ),
            )

        val expected =
            """
            package com.example

            import androidx.navigation.NavBackStackEntry
            import com.cramsan.framework.core.compose.navigation.WebRouteRegistry
            import com.cramsan.framework.core.compose.navigation.webRouteEntry

            internal object MainDestinationWebRoutes {
                private val flyerListEntry = webRouteEntry<MainDestination.FlyerListDestination>("/")
                private val flyerDetailEntry = webRouteEntry<MainDestination.FlyerDetailDestination>("/flyer")

                private val registry = WebRouteRegistry<MainDestination>(
                    listOf(
                        flyerListEntry,
                        flyerDetailEntry,
                    ),
                )

                fun toWebPath(destination: MainDestination): String = when (destination) {
                    is MainDestination.FlyerListDestination -> flyerListEntry.route.toWebPath(destination)
                    is MainDestination.FlyerDetailDestination -> flyerDetailEntry.route.toWebPath(destination)
                }

                fun fromWebPath(path: String): MainDestination? = registry.fromWebPath(path)

                fun toWebPath(entry: NavBackStackEntry): String? = registry.toWebPath(entry)
            }

            """.trimIndent()

        assertEquals(expected, source)
    }

    @Test
    fun `generates an object with an empty registry when there are no entries`() {
        val source =
            generateWebRoutesSource(
                packageName = "com.example",
                rootName = "EmptyDestination",
                entries = emptyList(),
            )

        val expected =
            """
            package com.example

            import androidx.navigation.NavBackStackEntry
            import com.cramsan.framework.core.compose.navigation.WebRouteRegistry
            import com.cramsan.framework.core.compose.navigation.webRouteEntry

            internal object EmptyDestinationWebRoutes {

                private val registry = WebRouteRegistry<EmptyDestination>(
                    listOf(
                    ),
                )

                fun toWebPath(destination: EmptyDestination): String = when (destination) {
                }

                fun fromWebPath(path: String): EmptyDestination? = registry.fromWebPath(path)

                fun toWebPath(entry: NavBackStackEntry): String? = registry.toWebPath(entry)
            }

            """.trimIndent()

        assertEquals(expected, source)
    }
}
