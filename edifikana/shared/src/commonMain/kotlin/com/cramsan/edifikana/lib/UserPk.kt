package com.cramsan.edifikana.lib

import com.cramsan.edifikana.lib.utils.requireNotBlank

/**
 * Represents a user primary key.
 */
@JvmInline
value class UserPk(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
