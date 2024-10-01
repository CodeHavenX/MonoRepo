package com.cramsan.edifikana.lib.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Requires that the value is not blank or null.
 *
 * @param value The value to check.
 * @param lazyMessage The message to use if the value is null.
 * @return The value if it is not blank or null.
 */
@OptIn(ExperimentalContracts::class)
fun requireNotBlank(value: String?, lazyMessage: (() -> Any)? = null): String {
    contract {
        returns() implies (value != null)
    }

    requireNotNull(value, lazyMessage ?: { "Required string was null." })
    require(value.isNotBlank(), lazyMessage ?: { "Required string was blank." })

    return value
}
