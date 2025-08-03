package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing a file ID.
 */
@JvmInline
value class FileId(val fileId: String) {
    override fun toString(): String = fileId
}