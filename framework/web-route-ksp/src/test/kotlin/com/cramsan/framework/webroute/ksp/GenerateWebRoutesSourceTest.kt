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

class GeneratePathNavigationSourceTest {
    @Test
    fun `generates an aggregator chaining every root, importing those outside its own package`() {
        val source =
            generatePathNavigationSource(
                packageName = "com.example.navigation",
                objectName = "ExamplePathNavigation",
                roots =
                listOf(
                    GeneratedRoot("com.example.auth", "AuthDestination"),
                    GeneratedRoot("com.example.home", "HomeDestination"),
                ),
            )

        val expected =
            """
            package com.example.navigation

            import androidx.navigation.NavBackStackEntry
            import com.cramsan.framework.core.compose.navigation.Destination
            import com.example.auth.AuthDestination
            import com.example.home.HomeDestination

            internal object ExamplePathNavigation {
                fun pathToDestination(path: String): Destination? =
                    AuthDestination.fromWebPath(path)
                        ?: HomeDestination.fromWebPath(path)

                fun entryToPath(entry: NavBackStackEntry): String? =
                    AuthDestination.toWebPath(entry)
                        ?: HomeDestination.toWebPath(entry)
            }

            """.trimIndent()

        assertEquals(expected, source)
    }

    @Test
    fun `interleaves root imports with the fixed framework import alphabetically`() {
        val source =
            generatePathNavigationSource(
                packageName = "com.cramsan.edifikana.client.lib.navigation",
                objectName = "EdifikanaPathNavigation",
                roots = listOf(GeneratedRoot("com.cramsan.edifikana.client.lib.features.auth", "AuthDestination")),
            )

        val expected =
            """
            package com.cramsan.edifikana.client.lib.navigation

            import androidx.navigation.NavBackStackEntry
            import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
            import com.cramsan.framework.core.compose.navigation.Destination

            internal object EdifikanaPathNavigation {
                fun pathToDestination(path: String): Destination? =
                    AuthDestination.fromWebPath(path)

                fun entryToPath(entry: NavBackStackEntry): String? =
                    AuthDestination.toWebPath(entry)
            }

            """.trimIndent()

        assertEquals(expected, source)
    }

    @Test
    fun `omits the import for a root in the same package as the aggregator`() {
        val source =
            generatePathNavigationSource(
                packageName = "com.example",
                objectName = "ExamplePathNavigation",
                roots = listOf(GeneratedRoot("com.example", "MainDestination")),
            )

        assertEquals(
            false,
            source.contains("import com.example.MainDestination"),
        )
    }

    @Test
    fun `generates a null-returning aggregator when there are no roots`() {
        val source =
            generatePathNavigationSource(
                packageName = "com.example.navigation",
                objectName = "EmptyPathNavigation",
                roots = emptyList(),
            )

        val expected =
            """
            package com.example.navigation

            import androidx.navigation.NavBackStackEntry
            import com.cramsan.framework.core.compose.navigation.Destination

            internal object EmptyPathNavigation {
                fun pathToDestination(path: String): Destination? =
                    null

                fun entryToPath(entry: NavBackStackEntry): String? =
                    null
            }

            """.trimIndent()

        assertEquals(expected, source)
    }
}
