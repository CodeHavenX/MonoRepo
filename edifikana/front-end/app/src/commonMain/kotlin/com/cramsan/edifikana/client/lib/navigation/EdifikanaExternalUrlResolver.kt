package com.cramsan.edifikana.client.lib.navigation

import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Maps a native custom-scheme deep link prefix to its canonical web path. Add an entry here
 * whenever a new custom-scheme deep link is introduced. The scheme's own payload (e.g. Supabase's
 * `code=`/`access_token=` auth params) is intentionally dropped here, not carried into the
 * canonical path — it's already consumed upstream (e.g. by `supabase.handleDeeplinks(intent)`
 * on Android) before this resolver runs, and the target destination doesn't declare those as
 * fields of its own.
 */
private val customSchemeAliases =
    mapOf(
        "edifikana://reset" to "/auth/set-new-password",
    )

/**
 * Resolves a raw external URL — a browser path, or a native custom-scheme deep link such as
 * `edifikana://reset?code=...` or `edifikana://reset#access_token=...&type=recovery` — into the
 * matching typed navigation [Destination], via the KSP-generated [EdifikanaPathNavigation], or
 * null if nothing claims it.
 */
fun edifikanaResolveExternalUrl(rawInput: String): Destination? {
    val canonicalPath = customSchemeAliases.entries.firstOrNull { rawInput.startsWith(it.key) }?.value
    return EdifikanaPathNavigation.pathToDestination(canonicalPath ?: rawInput)
}
