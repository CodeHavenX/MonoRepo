package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a file ID.
 */
@Serializable
@JvmInline
value class AssetId(val assetId: String) : PathParam {
    override fun toString(): String = assetId
}
