package com.cramsan.edifikana.lib
@JvmInline
value class EventLogRecordPK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
