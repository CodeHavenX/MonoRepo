package com.cramsan.edifikana.lib

@JvmInline
value class TimeCardRecordPK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
