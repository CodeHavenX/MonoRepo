package com.cramsan.framework.annotations

/**
 * Marks a `WebDestination` sealed subclass with its canonical browser URL path (e.g. "/flyer").
 *
 * Every direct subclass of a sealed class implementing `WebDestination` must carry this
 * annotation. A KSP processor reads it to generate the routing object (`<Name>WebRoutes`) that
 * implements `toWebPath()`/`fromWebPath()`/`toWebPath(NavBackStackEntry)` for that sealed class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class WebPath(val path: String)
