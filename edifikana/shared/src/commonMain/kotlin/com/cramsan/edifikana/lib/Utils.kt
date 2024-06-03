package com.cramsan.edifikana.lib

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun requireNotBlank(value: String?, lazyMessage: (() -> Any)? = null): String {
    contract {
        returns() implies (value != null)
    }

    requireNotNull(value, lazyMessage ?: { "Required string was null." })
    require(value.isNotBlank(), lazyMessage ?: { "Required string was blank." })

    return value
}
