package com.cramsan.flyerboard.client.lib.navigation

import androidx.navigation.NavHostController

/** No-op JVM Desktop implementation — the desktop app does not use URL routing. */
actual class BrowserNavigator actual constructor() {
    /** No-op on JVM Desktop. */
    actual fun attach(navController: NavHostController) = Unit

    /** Always returns null on JVM Desktop. */
    actual fun getInitialPath(): String? = null
}
