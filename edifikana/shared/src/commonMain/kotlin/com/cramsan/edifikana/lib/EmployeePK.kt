package com.cramsan.edifikana.lib

import com.cramsan.edifikana.lib.utils.requireNotBlank

@JvmInline
value class EmployeePK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
