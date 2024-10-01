package com.cramsan.edifikana.lib

import com.cramsan.edifikana.lib.utils.requireNotBlank

@JvmInline
value class EventLogRecordPK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
