package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing a file ID.
 */
@JvmInline
value class AssetId(val assetId: String) {
    override fun toString(): String = assetId
}