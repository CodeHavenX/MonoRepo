package com.cramsan.edifikana.lib

@JvmInline
value class UserPk(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
