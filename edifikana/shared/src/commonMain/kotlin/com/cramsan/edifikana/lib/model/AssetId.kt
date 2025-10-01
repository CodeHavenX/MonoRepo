package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a file ID.
 */
@Serializable
@JvmInline
value class AssetId(val assetId: String) {
    override fun toString(): String = assetId
}
