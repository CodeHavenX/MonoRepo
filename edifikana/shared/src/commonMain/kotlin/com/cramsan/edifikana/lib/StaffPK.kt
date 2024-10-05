package com.cramsan.edifikana.lib

import com.cramsan.edifikana.lib.utils.requireNotBlank

/**
 * Represents a time card record primary key.
 */
@JvmInline
value class StaffPK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
