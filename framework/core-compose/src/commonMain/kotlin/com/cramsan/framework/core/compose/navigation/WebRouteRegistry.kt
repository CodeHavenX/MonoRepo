package com.cramsan.framework.core.compose.navigation

import androidx.navigation.NavBackStackEntry
import kotlin.reflect.KClass

/**
 * Bundles a [WebRoute] for [D] together with the back-stack lookup for [D].
 *
 * Created via [webRouteEntry], which captures [D] as a reified type so [toWebPathIfRoute]
 * can resolve [NavBackStackEntry.toWebPathIfRoute] for [D].
 */
class WebRouteEntry<D : WebDestination> @PublishedApi internal constructor(
    val klass: KClass<D>,
    val route: WebRoute<D>,
    private val backStackToWebPath: (NavBackStackEntry) -> String?,
) {
    /** Returns the canonical URL for [entry] if it is an instance of [D], or null otherwise. */
    fun toWebPathIfRoute(entry: NavBackStackEntry): String? = backStackToWebPath(entry)
}

/**
 * Creates a [WebRouteEntry] for [D] at [path].
 */
inline fun <reified D : WebDestination> webRouteEntry(path: String): WebRouteEntry<D> =
    WebRouteEntry(D::class, webRoute(path)) { entry -> entry.toWebPathIfRoute<D>() }

/**
 * Single source of truth for the bidirectional mapping between a sealed [T] hierarchy and its
 * canonical browser URLs.
 *
 * Build [entries] with [webRouteEntry], one per [T] subclass:
 *
 * ```
 * private val registry = WebRouteRegistry(
 *     listOf(
 *         fooEntry,
 *         barEntry,
 *     ),
 * )
 *
 * fun fromWebPath(path: String): MyDestination? = registry.fromWebPath(path)
 * fun toWebPath(entry: NavBackStackEntry): String? = registry.toWebPath(entry)
 * ```
 *
 * [registeredClasses] can be compared against `MyDestination::class.sealedSubclasses` in a JVM
 * test to catch a destination that was added without a corresponding entry.
 */
class WebRouteRegistry<T : WebDestination>(private val entries: List<WebRouteEntry<out T>>) {
    /** Parses [path] and returns the matching destination, or null if unrecognised. */
    fun fromWebPath(path: String): T? =
        entries.firstNotNullOfOrNull { entry ->
            @Suppress("UNCHECKED_CAST")
            entry.route.fromWebPath(path) as T?
        }

    /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
    fun toWebPath(entry: NavBackStackEntry): String? =
        entries.firstNotNullOfOrNull { it.toWebPathIfRoute(entry) }

    /** The set of destination classes covered by this registry. */
    val registeredClasses: Set<KClass<out T>>
        get() = entries.mapTo(mutableSetOf()) { it.klass }
}
