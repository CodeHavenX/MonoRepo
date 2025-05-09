package com.cramsan.framework.core.compose.resources

import org.jetbrains.compose.resources.StringResource

/**
 * Interface for a string provider. This is used to provide strings from resources.
 */
class ComposeStringProvider : StringProvider {

    /**
     * Get a string from resources.
     *
     * @param res The string resource to get.
     * @return The string from resources.
     */
    override suspend fun getString(res: StringResource): String {
        return getString(res)
    }
}
