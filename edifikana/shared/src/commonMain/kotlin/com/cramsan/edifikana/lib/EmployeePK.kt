package com.cramsan.edifikana.lib

@JvmInline
value class EmployeePK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
