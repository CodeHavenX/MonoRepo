package com.cramsan.edifikana.lib.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Requires that the value is not blank or null.
 *
 * @param value The value to check.
 * @param message The message to use if the value is null.
 * @return The value if it is not blank or null.
 */
@OptIn(ExperimentalContracts::class)
fun requireNotBlank(value: String?, message: String? = null): String {
    contract {
        returns() implies (value != null)
    }

    requireNotNull(value, { message ?: "Required string was null." })
    require(value.isNotBlank(), { message ?: "Required string was blank." })

    return value
}
